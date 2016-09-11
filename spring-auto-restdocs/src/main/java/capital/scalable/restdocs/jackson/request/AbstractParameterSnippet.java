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

package capital.scalable.restdocs.jackson.request;

import static capital.scalable.restdocs.jackson.OperationAttributeHelper.getConstraintReader;
import static capital.scalable.restdocs.jackson.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.jackson.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.jackson.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static java.util.Collections.singletonList;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.util.StringUtils.hasLength;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.jackson.constraints.ConstraintReader;
import capital.scalable.restdocs.jackson.javadoc.JavadocReader;
import capital.scalable.restdocs.jackson.snippet.StandardTableSnippet;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.web.method.HandlerMethod;

abstract class AbstractParameterSnippet<A extends Annotation> extends StandardTableSnippet {
    protected AbstractParameterSnippet(String snippetName, Map<String, Object> attributes) {
        super(snippetName, attributes);
    }

    @Override
    protected List<FieldDescriptor> createFieldDescriptors(Operation operation,
            HandlerMethod handlerMethod) {
        JavadocReader javadocReader = getJavadocReader(operation);
        ConstraintReader constraintReader = getConstraintReader(operation);

        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        for (MethodParameter param : handlerMethod.getMethodParameters()) {
            A annot = getAnnotation(param);
            if (annot != null) {
                addFieldDescriptor(handlerMethod, javadocReader, constraintReader, fieldDescriptors,
                        param, annot);
            }
        }
        return fieldDescriptors;
    }

    private void addFieldDescriptor(HandlerMethod handlerMethod,
            JavadocReader javadocReader, ConstraintReader constraintReader,
            List<FieldDescriptor> fieldDescriptors, MethodParameter param, A annot) {
        String javaParameterName = param.getParameterName();
        String pathName = getPath(annot);

        String parameterName = hasLength(pathName) ? pathName : javaParameterName;
        String parameterType = param.getParameterType().getSimpleName();
        String description = javadocReader.resolveMethodParameterComment(
                handlerMethod.getBeanType(), handlerMethod.getMethod().getName(),
                javaParameterName);

        FieldDescriptor descriptor = fieldWithPath(parameterName)
                .type(parameterType)
                .description(description);

        Attribute constraints = constraintAttribute(param, constraintReader);
        Attribute optionals = optionalsAttribute(param, annot);
        descriptor.attributes(constraints, optionals);

        fieldDescriptors.add(descriptor);
    }

    protected Attribute constraintAttribute(MethodParameter param,
            ConstraintReader constraintReader) {
        return new Attribute(CONSTRAINTS_ATTRIBUTE, constraintReader.getConstraintMessages(param));
    }

    protected Attribute optionalsAttribute(MethodParameter param, A annot) {
        return new Attribute(OPTIONAL_ATTRIBUTE, singletonList(!isRequired(annot)));
    }

    protected abstract boolean isRequired(A annot);

    protected abstract String getPath(A annot);

    abstract A getAnnotation(MethodParameter param);
}
