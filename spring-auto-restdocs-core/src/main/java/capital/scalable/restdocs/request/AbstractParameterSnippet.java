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

package capital.scalable.restdocs.request;

import static capital.scalable.restdocs.OperationAttributeHelper.getConstraintReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.DEFAULT_VALUE_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.DEPRECATED_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static capital.scalable.restdocs.util.FieldDescriptorUtil.assertAllDocumented;
import static capital.scalable.restdocs.util.TypeUtil.determineTypeName;
import static java.util.Collections.singletonList;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.util.StringUtils.hasLength;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.section.SectionSupport;
import capital.scalable.restdocs.snippet.StandardTableSnippet;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;

abstract class AbstractParameterSnippet<A extends Annotation> extends StandardTableSnippet
        implements SectionSupport {
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

        if (shouldFailOnUndocumentedParams()) {
            assertAllDocumented(fieldDescriptors, getHeader().toLowerCase());
        }

        return fieldDescriptors;
    }

    private void addFieldDescriptor(HandlerMethod handlerMethod,
            JavadocReader javadocReader, ConstraintReader constraintReader,
            List<FieldDescriptor> fieldDescriptors, MethodParameter param, A annot) {
        String javaParameterName = param.getParameterName();
        String pathName = getPath(annot);

        String parameterName = hasLength(pathName) ? pathName : javaParameterName;
        String parameterTypeName = determineTypeName(param.getParameterType());
        String description = javadocReader.resolveMethodParameterComment(
                handlerMethod.getBeanType(), handlerMethod.getMethod().getName(),
                javaParameterName);

        FieldDescriptor descriptor = fieldWithPath(parameterName)
                .type(parameterTypeName)
                .description(description);

        Attribute constraints = constraintAttribute(param, constraintReader);
        Attribute optionals = optionalsAttribute(param, annot);
        Attribute deprecated = deprecatedAttribute(param, annot, javadocReader);
        final Attribute defaultValue = defaultValueAttribute(annot);
        if (defaultValue == null) {
            descriptor.attributes(constraints, optionals, deprecated);
        } else {
            descriptor.attributes(constraints, optionals, deprecated, defaultValue);
        }

        fieldDescriptors.add(descriptor);
    }

    abstract protected String getDefaultValue(A annotation);

    protected boolean isCustomDefaultValue(final String defaultValue) {
        return defaultValue != null && !ValueConstants.DEFAULT_NONE.equals(defaultValue);
    }

    protected Attribute constraintAttribute(MethodParameter param,
            ConstraintReader constraintReader) {
        return new Attribute(CONSTRAINTS_ATTRIBUTE, constraintReader.getConstraintMessages(param));
    }

    protected Attribute optionalsAttribute(MethodParameter param, A annot) {
        return new Attribute(OPTIONAL_ATTRIBUTE, singletonList(!isRequired(param, annot)));
    }

    protected Attribute deprecatedAttribute(MethodParameter param, A annot,
            JavadocReader javadocReader) {
        return new Attribute(DEPRECATED_ATTRIBUTE,
                param.getParameterAnnotation(Deprecated.class) != null ? "" : null);
    }

    protected Attribute defaultValueAttribute(A annot) {
        final String defaultValue = getDefaultValue(annot);

        return isCustomDefaultValue(defaultValue) ?
                new Attribute(DEFAULT_VALUE_ATTRIBUTE, defaultValue) : null;
    }

    protected abstract boolean isRequired(MethodParameter param, A annot);

    protected abstract String getPath(A annot);

    abstract A getAnnotation(MethodParameter param);

    protected abstract boolean shouldFailOnUndocumentedParams();

    @Override
    public String getFileName() {
        return getSnippetName();
    }

    @Override
    public boolean hasContent(Operation operation) {
        for (MethodParameter param : getHandlerMethod(operation).getMethodParameters()) {
            A annot = getAnnotation(param);
            if (annot != null) {
                return true;
            }
        }
        return false;
    }
}
