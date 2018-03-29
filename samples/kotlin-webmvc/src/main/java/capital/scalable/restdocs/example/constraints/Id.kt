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
package capital.scalable.restdocs.example.constraints

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, FIELD, ANNOTATION_CLASS, CONSTRUCTOR, VALUE_PARAMETER)
@Retention(RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [])
@Pattern(regexp = "[a-zA-Z0-9]{20}")
annotation class Id(
        val message: String = "Must be a valid ID",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = [])

