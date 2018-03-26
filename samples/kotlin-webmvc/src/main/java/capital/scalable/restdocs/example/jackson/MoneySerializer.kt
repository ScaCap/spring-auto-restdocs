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
package capital.scalable.restdocs.example.jackson

import java.io.IOException

import capital.scalable.restdocs.example.items.Money
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
import com.fasterxml.jackson.databind.ser.std.StdSerializer

internal class MoneySerializer : StdSerializer<Money>(Money::class.java) {

    @Throws(IOException::class)
    override fun serialize(value: Money, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeString("${value.numberStripped} ${value.currencyCode}")
    }

    @Throws(JsonMappingException::class)
    override fun acceptJsonFormatVisitor(visitor: JsonFormatVisitorWrapper, typeHint: JavaType) {
        visitor.expectStringFormat(typeHint)
    }
}
