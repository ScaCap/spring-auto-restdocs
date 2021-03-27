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

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.name.Names
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.dokka.Content
import org.jetbrains.dokka.DefaultPackageListService
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.DokkaConsoleLogger
import org.jetbrains.dokka.DokkaGenerator
import org.jetbrains.dokka.DokkaLogger
import org.jetbrains.dokka.FormatService
import org.jetbrains.dokka.Formats.FormatDescriptor
import org.jetbrains.dokka.Generator
import org.jetbrains.dokka.KotlinLanguageService
import org.jetbrains.dokka.LanguageService
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.NodeLocationAwareGenerator
import org.jetbrains.dokka.PackageListService
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.Utilities.DokkaRunModule
import org.jetbrains.dokka.Utilities.ServiceLocator
import org.junit.Test
import java.io.File

class JsonFormatIntegrationTest {

    @Test
    fun `Should load descriptor from properties file, bind and use it`() {
        DokkaGenerator(DokkaConfigurationImpl(), DokkaConsoleLogger).generate()
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

class PassConfigurationImpl(
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
    override val jdkVersion: Int = 8,
    override val sourceLinks: List<DokkaConfiguration.SourceLinkDefinition> = emptyList(),
    override val perPackageOptions: List<DokkaConfiguration.PackageOptions> = emptyList(),
    override val externalDocumentationLinks: List<DokkaConfiguration.ExternalDocumentationLink> = emptyList(),
    override val languageVersion: String? = null,
    override val apiVersion: String? = null,
    override val noStdlibLink: Boolean = false,
    override val noJdkLink: Boolean = false,
    override val suppressedFiles: List<String> = emptyList(),
    override val collectInheritedExtensionsFromLibraries: Boolean = false,
    override val analysisPlatform: Platform = Platform.DEFAULT,
    override val targets: List<String> = emptyList(),
    override val sinceKotlin: String? = null
) : DokkaConfiguration.PassConfiguration
