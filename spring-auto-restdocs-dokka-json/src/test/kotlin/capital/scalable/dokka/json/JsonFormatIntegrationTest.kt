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

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Guice
import com.google.inject.name.Names
import org.jetbrains.dokka.Utilities.ServiceLocator
import org.junit.Test
import org.jetbrains.dokka.Formats.FormatDescriptor
import org.jetbrains.dokka.DokkaLogger
import org.jetbrains.dokka.DokkaConsoleLogger
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.Content
import org.jetbrains.dokka.NodeKind
import java.io.File
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.Utilities.DokkaRunModule
import org.jetbrains.dokka.FormatService

import org.jetbrains.dokka.Generator
import org.jetbrains.dokka.KotlinLanguageService
import org.jetbrains.dokka.LanguageService
import org.jetbrains.dokka.NodeLocationAwareGenerator
import org.jetbrains.dokka.PackageListService
import org.jetbrains.dokka.DefaultPackageListService

import org.jetbrains.dokka.DokkaGenerator

class JsonFormatDescriptor2Test {

    @Test
    fun `Should load descriptor from properties file, bind and use it`() {
        DokkaGenerator(DokkaConfigurationImpl(), DokkaConsoleLogger).generate()
    }

    @Test
    fun `Should load descriptor from properties file and bind it correctly x`() {
        println("Start")

        val globalInjector = Guice.createInjector(DokkaRunModule(DokkaConfigurationImpl()))
        val injector = globalInjector.createChildInjector(JsonFormatDescriptor2TestModule())

        val formatService = injector.getInstance(FormatService::class.java)
        println("1 $formatService")

        val jsonFormatService = injector.getInstance(JsonFormatService::class.java)
        println("2 $jsonFormatService")

        val jsonFileGenerator = injector.getInstance(JsonFileGenerator::class.java)
        val x = jsonFileGenerator.location(DocumentationNode("", Content.Empty, NodeKind.Parameter))
        val y = x.path.contains("format-descriptor-test")
        println("$x - $y")
    }
}

class JsonFormatDescriptor2TestModule(): Module {
    override fun configure(binder: Binder) {
        //val outputDir: File = FileUtil.createTempDirectory("dokka-json", "format-descriptor-test")
        //binder.bind(File::class.java).annotatedWith(Names.named("outputDir")).toInstance(outputDir)
        binder.bind(DokkaLogger::class.java).toInstance(DokkaConsoleLogger)

        /*binder.bind(Generator::class.java).to(NodeLocationAwareGenerator::class.java)
        binder.bind(NodeLocationAwareGenerator::class.java).to(JsonFileGenerator::class.java)
        binder.bind(JsonFileGenerator::class.java) // https://github.com/google/guice/issues/847
        binder.bind(LanguageService::class.java).to(KotlinLanguageService::class.java)
        binder.bind(FormatService::class.java).to(JsonFormatService::class.java)
        binder.bind(PackageListService::class.java).to(DefaultPackageListService::class.java)*/
    }
}

data class SourceLinkDefinitionImpl(override val path: String,
                                    override val url: String,
                                    override val lineSuffix: String?) : DokkaConfiguration.SourceLinkDefinition {
    companion object {
        fun parseSourceLinkDefinition(srcLink: String): DokkaConfiguration.SourceLinkDefinition {
            val (path, urlAndLine) = srcLink.split('=')
            return SourceLinkDefinitionImpl(
                File(path).canonicalPath,
                urlAndLine.substringBefore("#"),
                urlAndLine.substringAfter("#", "").let { if (it.isEmpty()) null else "#$it" })
        }
    }
}

class SourceRootImpl(path: String) : DokkaConfiguration.SourceRoot {
    override val path: String = File(path).absolutePath

    companion object {
        fun parseSourceRoot(sourceRoot: String): DokkaConfiguration.SourceRoot = SourceRootImpl(sourceRoot)
    }
}

data class PackageOptionsImpl(override val prefix: String,
                              override val includeNonPublic: Boolean = false,
                              override val reportUndocumented: Boolean = true,
                              override val skipDeprecated: Boolean = false,
                              override val suppress: Boolean = false) : DokkaConfiguration.PackageOptions

class DokkaConfigurationImpl(
    override val outputDir: String = "",
    override val format: String = "auto-restdocs-json",
    override val generateIndexPages: Boolean = false,
    override val cacheRoot: String? = null,
    override val impliedPlatforms: List<String> = emptyList(),
    override val passesConfigurations: List<DokkaConfiguration.PassConfiguration> = listOf(PassConfigurationImpl())
) : DokkaConfiguration

class PassConfigurationImpl (
    override val classpath: List<String> = emptyList(),
    override val moduleName: String = "",
    override val sourceRoots: List<DokkaConfiguration.SourceRoot> = emptyList(),
    override val samples: List<String> = emptyList(),
    override val includes: List<String> = emptyList(),
    override val includeNonPublic: Boolean = false,
    override val includeRootPackage: Boolean = false,
    override val reportUndocumented: Boolean = false,
    override val skipEmptyPackages: Boolean = false,
    override val skipDeprecated: Boolean = false,
    override val jdkVersion: Int = 6,
    override val sourceLinks: List<DokkaConfiguration.SourceLinkDefinition> = emptyList(),
    override val perPackageOptions: List<DokkaConfiguration.PackageOptions> = emptyList(),
    externalDocumentationLinks: List<DokkaConfiguration.ExternalDocumentationLink> = emptyList(),
    override val languageVersion: String? = null,
    override val apiVersion: String? = null,
    override val noStdlibLink: Boolean = false,
    override val noJdkLink: Boolean = false,
    override val suppressedFiles: List<String> = emptyList(),
    override val collectInheritedExtensionsFromLibraries: Boolean = false,
    override val analysisPlatform: Platform = Platform.DEFAULT,
    override val targets: List<String> = emptyList(),
    override val sinceKotlin: String? = null
): DokkaConfiguration.PassConfiguration {
    private val defaultLinks = run {
        val links = mutableListOf<DokkaConfiguration.ExternalDocumentationLink>()
        if (!noJdkLink)
            links += DokkaConfiguration.ExternalDocumentationLink.Builder("https://docs.oracle.com/javase/$jdkVersion/docs/api/").build()

        if (!noStdlibLink)
            links += DokkaConfiguration.ExternalDocumentationLink.Builder("https://kotlinlang.org/api/latest/jvm/stdlib/").build()
        links
    }
    override val externalDocumentationLinks = defaultLinks + externalDocumentationLinks
}
