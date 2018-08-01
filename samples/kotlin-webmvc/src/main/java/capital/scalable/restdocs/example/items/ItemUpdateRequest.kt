/*-
 * #%L
 * Spring Auto REST Docs Kotlin Web MVC Example Project
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
package capital.scalable.restdocs.example.items

import capital.scalable.restdocs.example.constraints.English
import capital.scalable.restdocs.example.constraints.German
import capital.scalable.restdocs.example.constraints.OneOf
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Kotlin object for the JSON request.
 */
internal data class ItemUpdateRequest(
        /**
         * Some information about the item.
         */
        @get:NotBlank(groups = [(English::class)])
        @get:Length(max = 20)
        @get:Size.List(Size(min = 2, max = 10, groups = arrayOf(German::class)), Size(min = 4, max = 12, groups = arrayOf(English::class)))
        val description: String,

        /**
         * Country dependent type of the item.
         */
        @get:Size(max = 1000)
        @get:OneOf.List(OneOf(value = arrayOf("klein", "groß"), groups = arrayOf(German::class)), OneOf(value = arrayOf("small", "big"), groups = arrayOf(English::class)))
        val type: String?)
