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

import com.google.inject.Inject
import com.google.inject.name.Named
import org.jetbrains.dokka.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

class JsonFileGenerator @Inject constructor(@Named("outputDir") override val root: File) : NodeLocationAwareGenerator {

    @set:Inject(optional = true)
    lateinit var formatService: FormatService

    override fun location(node: DocumentationNode): Location {
        return FileLocation(fileForNode(node, formatService.linkExtension))
    }

    private fun fileForNode(node: DocumentationNode, extension: String = ""): File {
        return File(root, relativePathToNode(node)).appendExtension(extension)
    }

    override fun buildPages(nodes: Iterable<DocumentationNode>) {

        for ((_, items) in nodes.groupBy { fileForNode(it, formatService.extension) }) {
            if (items.any { it.kind == NodeKind.Class }) {
                val location = locationOverride(items.find { it.kind == NodeKind.Class }!!)
                val file = location.file
                file.parentFile?.mkdirsOrFail()
                try {
                    FileOutputStream(file).use {
                        OutputStreamWriter(it, Charsets.UTF_8).use { writer ->
                            writer.write(formatService.format(location, items))
                        }
                    }
                } catch (e: Throwable) {
                    println(e)
                }
            }
            buildPages(items.flatMap { it.members })
        }
    }

    private fun locationOverride(node: DocumentationNode): FileLocation {
        val path = node.path
            // Remove class name. It is appended again below.
            .dropLast(1)
            .filter { it.name.isNotEmpty() && it.kind != NodeKind.Module }
            .joinToString("") {
                if (it.kind == NodeKind.Class) {
                    // Parent class if the node is a nested class
                    "${it.name}."
                } else {
                    // Turn package dots into folders
                    "${it.name.replace(".", "/")}/"
                }
            }
        val className = node.name
        return FileLocation(File(root.path, "$path$className.json"))
    }

    override fun buildOutlines(nodes: Iterable<DocumentationNode>) {}

    override fun buildSupportFiles() {}

    override fun buildPackageList(nodes: Iterable<DocumentationNode>) {}

    private fun File.mkdirsOrFail() {
        if (!mkdirs() && !exists()) {
            throw IOException("Failed to create directory $this")
        }
    }
}
