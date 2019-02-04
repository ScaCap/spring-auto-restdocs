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
package capital.scalable.restdocs.payload;

import static capital.scalable.restdocs.OperationAttributeHelper.getConstraintReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getObjectMapper;
import static capital.scalable.restdocs.i18n.SnippetTranslationResolver.translate;
import static capital.scalable.restdocs.util.FieldDescriptorUtil.assertAllDocumented;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.jackson.FieldDescriptors;
import capital.scalable.restdocs.jackson.FieldDocumentationGenerator;
import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.section.SectionSupport;
import capital.scalable.restdocs.snippet.StandardTableSnippet;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.web.method.HandlerMethod;

public abstract class AbstractJacksonFieldSnippet extends StandardTableSnippet implements SectionSupport {

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

    protected FieldDescriptors createFieldDescriptors(Operation operation,
            HandlerMethod handlerMethod) {
        ObjectMapper objectMapper = getObjectMapper(operation);

        JavadocReader javadocReader = getJavadocReader(operation);
        ConstraintReader constraintReader = getConstraintReader(operation);

        Type type = getType(handlerMethod);
        if (type == null) {
            return new FieldDescriptors();
        }

        try {
            FieldDescriptors fieldDescriptors = resolveFieldDescriptors(type, objectMapper,
                    javadocReader, constraintReader);

            if (shouldFailOnUndocumentedFields()) {
                assertAllDocumented(fieldDescriptors.values(), translate(getHeaderKey()).toLowerCase());
            }
            return fieldDescriptors;
        } catch (JsonMappingException e) {
            throw new JacksonFieldProcessingException("Error while parsing fields", e);
        }
    }

    protected Type firstGenericType(MethodParameter param) {
        Type type = param.getGenericParameterType();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            return Object.class;
        }
    }

    protected abstract Type getType(HandlerMethod method);

    protected abstract boolean shouldFailOnUndocumentedFields();

    protected boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type) || Stream.class.isAssignableFrom(type) ||
                (SCALA_TRAVERSABLE != null && SCALA_TRAVERSABLE.isAssignableFrom(type));
    }

    private FieldDescriptors resolveFieldDescriptors(Type type, ObjectMapper objectMapper,
            JavadocReader javadocReader, ConstraintReader constraintReader) throws JsonMappingException {
        FieldDocumentationGenerator generator = new FieldDocumentationGenerator(
                objectMapper.writer(), objectMapper.getDeserializationConfig(), javadocReader,
                constraintReader);
        return generator.generateDocumentation(type, objectMapper.getTypeFactory());
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
