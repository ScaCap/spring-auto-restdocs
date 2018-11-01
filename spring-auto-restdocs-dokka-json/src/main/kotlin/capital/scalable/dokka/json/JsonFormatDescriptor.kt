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

import com.google.inject.Binder
import org.jetbrains.dokka.DefaultPackageListService
import org.jetbrains.dokka.FormatService
import org.jetbrains.dokka.Formats.DefaultAnalysisComponent
import org.jetbrains.dokka.Formats.FormatDescriptor
import org.jetbrains.dokka.Generator
import org.jetbrains.dokka.Kotlin.KotlinDescriptorSignatureProvider
import org.jetbrains.dokka.KotlinJavaDocumentationBuilder
import org.jetbrains.dokka.KotlinLanguageService
import org.jetbrains.dokka.KotlinPackageDocumentationBuilder
import org.jetbrains.dokka.LanguageService
import org.jetbrains.dokka.PackageListService
import org.jetbrains.dokka.Samples.DefaultSampleProcessingService
import org.jetbrains.dokka.Samples.SampleProcessingService
import org.jetbrains.dokka.Utilities.bind
import org.jetbrains.dokka.Utilities.toType
import kotlin.reflect.KClass

class JsonFormatDescriptor : FormatDescriptor, DefaultAnalysisComponent {

    override fun configureOutput(binder: Binder): Unit = with(binder) {
        bind<Generator>() toType JsonFileGenerator::class
        bind<LanguageService>() toType KotlinLanguageService::class
        bind<FormatService>() toType JsonFormatService::class
        bind<PackageListService>() toType DefaultPackageListService::class
    }

    override val javaDocumentationBuilderClass = KotlinJavaDocumentationBuilder::class
    override val descriptorSignatureProvider = KotlinDescriptorSignatureProvider::class
    override val packageDocumentationBuilderClass = KotlinPackageDocumentationBuilder::class
    override val sampleProcessingService: KClass<out SampleProcessingService> = DefaultSampleProcessingService::class
}
