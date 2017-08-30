/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package capital.scalable.restdocs.jackson;

import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class FieldDocumentationArrayVisitor extends JsonArrayFormatVisitor.Base {

    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final Set<JavaType> visited;

    public FieldDocumentationArrayVisitor(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, Set<JavaType> visited) {
        super(provider);
        this.context = context;
        this.path = path;
        this.visited = visited;
    }

    @Override
    public void itemsFormat(JsonFormatVisitable handler, JavaType elementType)
            throws JsonMappingException {
        String elementPath = path + "[]";
        JsonFormatVisitorWrapper visitor = new FieldDocumentationVisitorWrapper(getProvider(),
                context, elementPath, null, visited);
        handler.acceptJsonFormatVisitor(visitor, elementType);
    }
}
