/*-
 * #%L
 * Spring Auto REST Docs Dokka JSON
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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

import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.dokka.DocumentationModule
import org.jetbrains.dokka.DokkaConsoleLogger
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.contentRootFromPath
import org.jetbrains.dokka.tests.ModelConfig
import org.jetbrains.dokka.tests.verifyJavaModel
import org.jetbrains.dokka.tests.verifyModel
import org.jetbrains.kotlin.cli.common.config.KotlinSourceRoot
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

class JsonFileGeneratorTest {

    private val analysisPlatform = Platform.common

    private val root: File = FileUtil.createTempDirectory("dokka-json", "file-generator-test")

    @Test
    fun `buildPages should generate JSON files for Kotlin classes and nested classes`() {
        val inputFile = "KotlinDataClass.kt"
        val expectedFiles = listOf("testdata/KotlinDataClass", "testdata/KotlinDataClass.NestedClass")
        verifyOutput(inputFile, verifier(expectedFiles))
    }

    @Test
    fun `buildPages should generate JSON files for Java classes and nested classes`() {
        val inputFile = "JavaClass.java"
        val expectedFiles = listOf("JavaClass", "JavaClass.NestedJavaClass")
        verifyJavaOutput(inputFile, verifier(expectedFiles))
    }

    private fun verifyOutput(inputFile: String, verifier: (DocumentationModule) -> Unit) {
        verifyModel(
            ModelConfig(
                roots = arrayOf(
                    KotlinSourceRoot("testdata/$inputFile", false)
                ),
                analysisPlatform = analysisPlatform
            ),
            verifier = verifier
        )
    }

    private fun verifyJavaOutput(inputFile: String, verifier: (DocumentationModule) -> Unit) {
        verifyJavaModel(
            "testdata/$inputFile",
            verifier = verifier
        )
    }

    private fun verifier(expectedFiles: List<String>): (DocumentationModule) -> Unit {
        val fileGenerator = initFileGenerator()
        val rootPath = File(root.path)
        return {
            fileGenerator.buildPages(listOf(it))
            expectedFiles.forEach { fileName ->
                val file = rootPath.resolve("$fileName.json")
                assertTrue("Expected file $fileName.json was not generated") { file.exists() }
            }
        }
    }

    private fun initFileGenerator(): JsonFileGenerator {
        val fileGenerator = JsonFileGenerator(root)
        fileGenerator.formatService = JsonFormatService(DokkaConsoleLogger)
        return fileGenerator
    }
}
