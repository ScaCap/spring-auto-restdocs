/*-
 * #%L
 * Spring Auto REST Docs Dokka JSON
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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

import org.jetbrains.dokka.DocumentationModule
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.DokkaConsoleLogger
import org.jetbrains.dokka.FileLocation
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.tests.verifyJavaOutput
import org.jetbrains.dokka.tests.verifyOutput
import org.junit.Test

private val tempLocation = FileLocation(createTempFile())

class JsonFormatServiceTest {

    private val jsonFormatService = JsonFormatService(DokkaConsoleLogger)

    @Test
    fun `Correct JSON file for a Kotlin data class should be created`() {
        verifyJsonNode("KotlinDataClass")
    }

    @Test
    fun `Correct JSON file for a nested Kotlin data class should be created`() {
        verifyNestedJsonNode("KotlinDataClass", ".NestedClass.json")
    }

    @Test
    fun `Correct JSON file for a Java class should be created`() {
        verifyJavaJsonNode("JavaClass")
    }

    @Test
    fun `Correct JSON file for a nested Java class should be created`() {
        verifyNestedJavaJsonNode("JavaClass", ".NestedJavaClass.json")
    }

    private fun verifyJsonNode(fileName: String) {
        verifyJsonNodes(fileName) { model -> model.members.single().members }
    }

    private fun verifyNestedJsonNode(fileName: String, outputExtension: String) {
        verifyJsonNodes(fileName, outputExtension) { model ->
            model.members.single().members.single().members(NodeKind.Class)
        }
    }

    private fun verifyJsonNodes(
        fileName: String,
        outputExtension: String = ".json",
        nodeFilter: (DocumentationModule) -> List<DocumentationNode>
    ) {
        verifyOutput("testdata/$fileName.kt", outputExtension) { model, output ->
            jsonFormatService.createOutputBuilder(output, tempLocation).appendNodes(nodeFilter(model))
        }
    }

    private fun verifyJavaJsonNode(fileName: String) {
        verifyJavaJsonNodes(fileName) { model -> model.members.single().members }
    }

    private fun verifyNestedJavaJsonNode(fileName: String, outputExtension: String) {
        verifyJavaJsonNodes(fileName, outputExtension) { model ->
            model.members.single().members.single().members(NodeKind.Class)
        }
    }

    private fun verifyJavaJsonNodes(
        fileName: String,
        outputExtension: String = ".json",
        nodeFilter: (DocumentationModule) -> List<DocumentationNode>
    ) {
        verifyJavaOutput("testdata/$fileName.java", outputExtension) { model, output ->
            jsonFormatService.createOutputBuilder(output, tempLocation).appendNodes(nodeFilter(model))
        }
    }
}
