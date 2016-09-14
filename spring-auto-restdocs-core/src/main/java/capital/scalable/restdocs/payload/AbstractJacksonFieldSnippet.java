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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.OperationAttributeHelper;
import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.jackson.FieldDocumentationGenerator;
import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.snippet.StandardTableSnippet;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.web.method.HandlerMethod;

abstract class AbstractJacksonFieldSnippet extends StandardTableSnippet {

    protected AbstractJacksonFieldSnippet(String type) {
        this(type, null);
    }

    protected AbstractJacksonFieldSnippet(String type, Map<String, Object> attributes) {
        super(type + "-fields", attributes);
    }

    protected List<FieldDescriptor> createFieldDescriptors(Operation operation,
            HandlerMethod handlerMethod) {
        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();

        Type type = getType(handlerMethod);
        if (type != null) {
            ObjectMapper objectMapper = OperationAttributeHelper.getObjectMapper(operation);
            JavadocReader javadocReader = OperationAttributeHelper.getJavadocReader(operation);
            ConstraintReader constraintReader = OperationAttributeHelper.getConstraintReader(
                    operation);

            try {
                FieldDocumentationGenerator generator = new FieldDocumentationGenerator(
                        objectMapper.writer(), javadocReader, constraintReader);

                List<FieldDescriptor> descriptors = generator
                        .generateDocumentation(type, objectMapper.getTypeFactory());

                fieldDescriptors.addAll(descriptors);
            } catch (JsonMappingException e) {
                throw new JacksonFieldProcessingException("Error while parsing fields", e);
            }
        }

        return fieldDescriptors;
    }

    protected Type firstGenericType(MethodParameter param) {
        return ((ParameterizedType) param.getGenericParameterType()).getActualTypeArguments()[0];
    }

    protected abstract Type getType(HandlerMethod method);
}
