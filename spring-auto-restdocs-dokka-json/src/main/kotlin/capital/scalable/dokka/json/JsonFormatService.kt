/*-
 * #%L
 * Spring Auto REST Docs Dokka JSON
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package capital.scalable.dokka.json

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.inject.Inject
import org.jetbrains.dokka.*

data class ClassDocumentation(
        val comment: String,
        val fields: Map<String, FieldDocumentation>,
        val methods: Map<String, MethodDocumentation>)

data class MethodDocumentation(
        val comment: String,
        val parameters: Map<String, String>,
        val tags: Map<String, String>)

data class FieldDocumentation(
        val comment: String,
        val tags: Map<String, String>)

open class JsonOutputBuilder(
        private val to: StringBuilder,
        private val logger: DokkaLogger) : FormattedOutputBuilder {

    private val mapper = jacksonObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true)

    override fun appendNodes(nodes: Iterable<DocumentationNode>) {
        val singleNode = nodes.singleOrNull()
        when (singleNode?.kind) {
            NodeKind.AllTypes -> Unit
            NodeKind.GroupNode -> Unit
            null -> Unit
            else -> appendClassDocumentation(singleNode)
        }
    }

    private fun appendClassDocumentation(node: DocumentationNode) {
        val fields = node.references(RefKind.Member)
                .filter { it.to.kind == NodeKind.Property || it.to.kind == NodeKind.Field }
                .map { propertyDocumentation(it.to) }
                .toMap()
        val methods = node.references(RefKind.Member)
                .filter { it.to.kind == NodeKind.Function }
                .map { functionDocumentation(it.to) }
                .toMap()
        val classDocumentation = ClassDocumentation(
                comment = extractContent(node),
                fields = fields,
                methods = methods)
        to.append(mapper.writeValueAsString(classDocumentation))
    }

    private fun propertyDocumentation(node: DocumentationNode): Pair<String, FieldDocumentation> {
        return Pair(node.name, FieldDocumentation(
                comment = extractContent(node),
                tags = tags(node)))
    }

    private fun functionDocumentation(node: DocumentationNode): Pair<String, MethodDocumentation> {
        val parameterComments = node.content.sections
                .filter { it.subjectName != null }
                .map {
                    Pair(it.subjectName!!, extractContent(it))
                }.toMap()
        return Pair(node.name, MethodDocumentation(
                comment = extractContent(node),
                parameters = parameterComments,
                tags = tags(node)))
    }

    private fun JsonOutputBuilder.tags(node: DocumentationNode): Map<String, String> {
        return node.content.sections
                .map { Pair(tagName(it.tag), extractContent(it.children)) }
                .toMap()
    }

    private fun tagName(tag: String): String {
        return when (tag) {
            ContentTags.SeeAlso -> "see"
            else -> tag.toLowerCase()
        }
    }

    private fun extractContent(node: DocumentationNode): String {
        return extractContent(node.content.children)
    }

    private fun extractContent(content: List<ContentNode>): String {
        return content.joinToString("") { extractContent(it) }
    }

    private fun extractContent(content: ContentNode): String {
        when (content) {
            is ContentText -> return content.text
            is ContentBlock -> return content.children.joinToString("") { extractContent(it) }
            is ContentNodeLink -> return content.node?.let { extractContent(it) } ?: ""
            is ContentEmpty -> return ""
            else -> logger.warn("Unhandled content node: $content")
        }
        return ""
    }
}

open class JsonFormatService @Inject constructor(private val logger: DokkaLogger) : FormatService {

    override val extension: String = "json"

    override fun createOutputBuilder(to: StringBuilder, location: Location): FormattedOutputBuilder =
            JsonOutputBuilder(to, logger)
}
