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

import org.jetbrains.dokka.*
import org.jetbrains.dokka.Formats.FormatDescriptor
import org.jetbrains.dokka.Kotlin.KotlinDescriptorSignatureProvider
import org.jetbrains.dokka.Samples.DefaultSampleProcessingService
import org.jetbrains.dokka.Samples.SampleProcessingService
import kotlin.reflect.KClass

class JsonFormatDescriptor : FormatDescriptor {
    override val formatServiceClass = JsonFormatService::class

    override val packageDocumentationBuilderClass = KotlinPackageDocumentationBuilder::class
    override val javaDocumentationBuilderClass = KotlinJavaDocumentationBuilder::class

    override val generatorServiceClass = JsonFileGenerator::class
    override val outlineServiceClass: KClass<out OutlineFormatService>? = null
    override val sampleProcessingService: KClass<out SampleProcessingService> = DefaultSampleProcessingService::class
    override val packageListServiceClass: KClass<out PackageListService>? = DefaultPackageListService::class
    override val descriptorSignatureProvider = KotlinDescriptorSignatureProvider::class
}
