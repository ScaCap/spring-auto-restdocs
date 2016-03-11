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

package capital.scalable.restdocs.jackson.payload;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.jackson.jackson.FieldDocumentationGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

/**
 * @author Florian Benz, Juraj Misur
 */
abstract class AbstractJacksonFieldSnippet extends TemplatedSnippet {

    protected AbstractJacksonFieldSnippet(String type) {
        this(type, null);
    }

    protected AbstractJacksonFieldSnippet(String type, Map<String, Object> attributes) {
        super(type + "-fields", attributes);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        HandlerMethod handlerMethod = getHandlerMethod(operation);

        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        if (handlerMethod != null) {
            Type type = getType(handlerMethod);
            if (type != null) {
                ObjectMapper objectMapper = getObjectMapper(operation);
                try {
                    fieldDescriptors.addAll(new FieldDocumentationGenerator(objectMapper.writer())
                            .generateDocumentation(type, objectMapper.getTypeFactory()));
                } catch (JsonMappingException e) {
                    throw new JacksonFieldProcessingException("Error while parsing fields", e);
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        enrichModel(model, handlerMethod);

        List<Map<String, Object>> fields = new ArrayList<>();
        model.put("fields", fields);
        for (FieldDescriptor descriptor : fieldDescriptors) {
            fields.add(createModelForDescriptor(descriptor));
        }
        model.put("hasFields", !fieldDescriptors.isEmpty());
        model.put("noFields", fieldDescriptors.isEmpty());
        return model;
    }

    private ObjectMapper getObjectMapper(Operation operation) {
        return (ObjectMapper) operation.getAttributes().get(ObjectMapper.class.getName());
    }

    private HandlerMethod getHandlerMethod(Operation operation) {
        return (HandlerMethod) operation.getAttributes().get(HandlerMethod.class.getName());
    }

    /**
     * Returns a model for the given {@code descriptor}
     *
     * @param descriptor the descriptor
     * @return the model
     */
    protected Map<String, Object> createModelForDescriptor(FieldDescriptor descriptor) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("path", descriptor.getPath());
        model.put("type", descriptor.getType().toString());
        model.put("optional", descriptor.isOptional());
        model.put("description", descriptor.getDescription().toString());
        return model;
    }

    protected Type firstGenericType(MethodParameter param) {
        return ((ParameterizedType) param.getGenericParameterType()).getActualTypeArguments()[0];
    }

    protected abstract Type getType(HandlerMethod method);

    protected void enrichModel(Map<String, Object> model, HandlerMethod handlerMethod) {
        // can be used to add additional fields
    }
}
