/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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
package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

class FieldDocumentationArrayVisitor extends JsonArrayFormatVisitor.Base {

    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final TypeRegistry typeRegistry;
    private final TypeFactory typeFactory;

    public FieldDocumentationArrayVisitor(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, TypeRegistry typeRegistry,
            TypeFactory typeFactory) {
        super(provider);
        this.context = context;
        this.path = path;
        this.typeRegistry = typeRegistry;
        this.typeFactory = typeFactory;
    }

    @Override
    public void itemsFormat(JsonFormatVisitable handler, JavaType elementType)
            throws JsonMappingException {
        String elementPath = path + "[]";
        JsonFormatVisitorWrapper visitor = new FieldDocumentationVisitorWrapper(getProvider(),
                context, elementPath, null, typeRegistry, typeFactory);
        handler.acceptJsonFormatVisitor(visitor, elementType);
    }
}
