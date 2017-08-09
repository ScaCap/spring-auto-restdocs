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

package capital.scalable.restdocs.payload;

import static capital.scalable.restdocs.OperationAttributeHelper.getConstraintReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getObjectMapper;
import static capital.scalable.restdocs.util.FieldDescriptorUtil.assertAllDocumented;
import static java.util.Collections.singletonList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.jackson.FieldDocumentationGenerator;
import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.section.SectionSupport;
import capital.scalable.restdocs.snippet.StandardTableSnippet;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.web.method.HandlerMethod;

abstract class AbstractJacksonFieldSnippet extends StandardTableSnippet implements SectionSupport {

    private static Class<?> SCALA_TRAVERSABLE;

    static {
        try {
            SCALA_TRAVERSABLE = Class.forName("scala.collection.Traversable");
        } catch (ClassNotFoundException ignored) {
            // It's fine to not be available outside of Scala projects.
        }
    }

    protected AbstractJacksonFieldSnippet(String snippetName, Map<String, Object> attributes) {
        super(snippetName, attributes);
    }

    protected Collection<FieldDescriptor> createFieldDescriptors(Operation operation,
            HandlerMethod handlerMethod) {
        ObjectMapper objectMapper = getObjectMapper(operation);
        ObjectWriter writer = objectMapper.writer();
        TypeFactory typeFactory = objectMapper.getTypeFactory();

        JavadocReader javadocReader = getJavadocReader(operation);
        ConstraintReader constraintReader = getConstraintReader(operation);

        Map<String, FieldDescriptor> fieldDescriptors = new LinkedHashMap<>();

        Type signatureType = getType(handlerMethod);
        if (signatureType != null) {
            try {
                for (Type type : resolveActualTypes(signatureType)) {
                    resolveFieldDescriptors(fieldDescriptors, type, objectMapper,
                            javadocReader, constraintReader);
                }
            } catch (JsonMappingException e) {
                throw new JacksonFieldProcessingException("Error while parsing fields", e);
            }
        }

        if (shouldFailOnUndocumentedFields()) {
            assertAllDocumented(fieldDescriptors.values(), getHeader().toLowerCase());
        }
        return fieldDescriptors.values();
    }

    protected Type firstGenericType(MethodParameter param) {
        return ((ParameterizedType) param.getGenericParameterType()).getActualTypeArguments()[0];
    }

    protected abstract Type getType(HandlerMethod method);

    protected abstract boolean shouldFailOnUndocumentedFields();

    protected boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type) ||
                (SCALA_TRAVERSABLE != null && SCALA_TRAVERSABLE.isAssignableFrom(type));
    }

    private Collection<Type> resolveActualTypes(Type type) {
        if (type instanceof Class) {
            JsonSubTypes jsonSubTypes = (JsonSubTypes) ((Class) type).getAnnotation(
                    JsonSubTypes.class);
            if (jsonSubTypes != null) {
                Collection<Type> types = new ArrayList<>();
                for (JsonSubTypes.Type subType : jsonSubTypes.value()) {
                    types.add(subType.value());
                }
                return types;
            }
        }

        return singletonList(type);
    }

    private void resolveFieldDescriptors(Map<String, FieldDescriptor> fieldDescriptors,
            Type type, ObjectMapper objectMapper, JavadocReader javadocReader,
            ConstraintReader constraintReader) throws JsonMappingException {
        FieldDocumentationGenerator generator = new FieldDocumentationGenerator(
                objectMapper.writer(), objectMapper.getDeserializationConfig(), javadocReader,
                constraintReader);
        List<FieldDescriptor> descriptors = generator.generateDocumentation(type,
                objectMapper.getTypeFactory());
        for (FieldDescriptor descriptor : descriptors) {
            if (fieldDescriptors.get(descriptor.getPath()) == null) {
                fieldDescriptors.put(descriptor.getPath(), descriptor);
            }
        }
    }

    @Override
    public String getFileName() {
        return getSnippetName();
    }

    @Override
    public boolean hasContent(Operation operation) {
        return getType(getHandlerMethod(operation)) != null;
    }
}
