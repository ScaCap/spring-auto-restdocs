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

import java.util.HashSet;
import java.util.Set;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;

public class FieldDocumentationVisitorWrapper implements JsonFormatVisitorWrapper {
    private SerializerProvider provider;
    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final InternalFieldInfo fieldInfo;
    private final Set<JavaType> visited;

    FieldDocumentationVisitorWrapper(FieldDocumentationVisitorContext context, String path,
            InternalFieldInfo fieldInfo, Set<JavaType> visited) {
        this(null, context, path, fieldInfo, visited);
    }

    FieldDocumentationVisitorWrapper(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, InternalFieldInfo fieldInfo,
            Set<JavaType> visited) {
        this.provider = provider;
        this.context = context;
        this.path = path;
        this.fieldInfo = fieldInfo;
        this.visited = visited;
    }

    public static FieldDocumentationVisitorWrapper create(JavadocReader javadocReader,
            ConstraintReader constraintReader, DeserializationConfig deserializationConfig) {
        return new FieldDocumentationVisitorWrapper(
                new FieldDocumentationVisitorContext(javadocReader, constraintReader,
                        deserializationConfig), "", null, new HashSet<JavaType>());
    }

    @Override
    public SerializerProvider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(SerializerProvider provider) {
        this.provider = provider;
    }

    @Override
    public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("Object");
        if (shouldExpand() && !wasVisited(type)) {
            return new FieldDocumentationObjectVisitor(provider, context, path,
                    withVisitedType(type));
        } else {
            return new JsonObjectFormatVisitor.Base();
        }
    }

    @Override
    public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("Array");
        if (shouldExpand() && !wasVisited(type)) {
            return new FieldDocumentationArrayVisitor(provider, context, path,
                    withVisitedType(type));
        } else {
            return new JsonArrayFormatVisitor.Base();
        }
    }

    @Override
    public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("String");
        return new JsonStringFormatVisitor.Base();
    }

    @Override
    public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("Decimal");
        return new JsonNumberFormatVisitor.Base();
    }

    @Override
    public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("Integer");
        return new JsonIntegerFormatVisitor.Base();
    }

    @Override
    public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("Boolean");
        return new JsonBooleanFormatVisitor.Base();
    }

    @Override
    public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("null");
        return new JsonNullFormatVisitor.Base();
    }

    @Override
    public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("Var");
        return new JsonAnyFormatVisitor.Base();
    }

    @Override
    public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent("Map");
        return new JsonMapFormatVisitor.Base(provider);
    }

    public FieldDocumentationVisitorContext getContext() {
        return context;
    }

    private void addFieldIfPresent(String jsonType) {
        if (fieldInfo != null) {
            context.addField(fieldInfo, jsonType);
        }
    }

    private boolean shouldExpand() {
        return fieldInfo == null || fieldInfo.shouldExpand();
    }

    private Set<JavaType> withVisitedType(JavaType type) {
        Set<JavaType> result = new HashSet<>(visited);
        result.add(type);
        return result;
    }

    private boolean wasVisited(JavaType type) {
        return visited.contains(type);
    }
}
