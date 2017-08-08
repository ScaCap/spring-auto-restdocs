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

import static capital.scalable.restdocs.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static capital.scalable.restdocs.util.FieldUtil.fromGetter;
import static capital.scalable.restdocs.util.FieldUtil.isGetter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.util.ReflectionUtils;

public class FieldDocumentationVisitorContext {
    private final List<FieldDescriptor> fields = new ArrayList<>();
    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    public FieldDocumentationVisitorContext(JavadocReader javadocReader,
            ConstraintReader constraintReader) {
        this.javadocReader = javadocReader;
        this.constraintReader = constraintReader;
    }

    public List<FieldDescriptor> getFields() {
        return fields;
    }

    public void addField(InternalFieldInfo info, String jsonType) {
        Class<?> javaFieldClass = info.getJavaBaseClass();
        String javaFieldName = info.getJavaFieldName();

        String comment = resolveComment(javaFieldClass, javaFieldName);
        String jsonFieldPath = info.getJsonFieldPath();

        FieldDescriptor fieldDescriptor = fieldWithPath(jsonFieldPath)
                .type(jsonType)
                .description(comment);

        Attribute constraints = constraintAttribute(javaFieldClass, javaFieldName);
        Attribute optionals = optionalAttribute(javaFieldClass, javaFieldName);
        fieldDescriptor.attributes(constraints, optionals);

        fields.add(fieldDescriptor);
    }

    private String resolveComment(Class<?> javaFieldClass, String javaFieldName) {
        String comment = javadocReader.resolveFieldComment(javaFieldClass, javaFieldName);
        if (isBlank(comment)) {
            // fallback if fieldName is getter method and comment is on the method itself
            comment = javadocReader.resolveMethodComment(javaFieldClass, javaFieldName);
        }
        if (isBlank(comment) && isGetter(javaFieldName)) {
            // fallback if fieldName is getter method but comment is on field itself
            comment = javadocReader.resolveFieldComment(javaFieldClass, fromGetter(javaFieldName));
        }
        return comment;
    }

    private Attribute constraintAttribute(Class<?> javaBaseClass, String javaFieldName) {
        return new Attribute(CONSTRAINTS_ATTRIBUTE,
                resolveConstraintDescriptions(javaBaseClass, javaFieldName));
    }

    private Attribute optionalAttribute(Class<?> javaBaseClass, String javaFieldName) {
        return new Attribute(OPTIONAL_ATTRIBUTE,
                resolveOptionalMessages(javaBaseClass, javaFieldName));
    }

    private List<String> resolveOptionalMessages(Class<?> javaBaseClass, String javaFieldName) {
        List<String> optionalMessages = new ArrayList<>();

        if (isPrimitive(javaBaseClass, javaFieldName)) {
            optionalMessages.add("true"); // jackson handles null for primitives, keeps the default
            return optionalMessages;
        }

        optionalMessages.addAll(constraintReader.getOptionalMessages(javaBaseClass, javaFieldName));

        // fallback to field itself if we got a getter and no annotation on it
        if (optionalMessages.isEmpty() && isGetter(javaFieldName)) {
            optionalMessages.addAll(
                    constraintReader.getOptionalMessages(javaBaseClass, fromGetter(javaFieldName)));
        }

        // if there was no default constraint resolved at all, default to optional=true
        if (!optionalMessages.contains("false")
                && !optionalMessages.contains("true")) {
            optionalMessages.add(0, "true");
        }

        return optionalMessages;
    }

    private boolean isPrimitive(Class<?> javaBaseClass, String javaFieldName) {
        Field field = ReflectionUtils.findField(javaBaseClass, javaFieldName);
        if (field == null && isGetter(javaFieldName)) {
            field = ReflectionUtils.findField(javaBaseClass, fromGetter(javaFieldName));
        }
        return field != null ? field.getType().isPrimitive() : false;
    }

    private List<String> resolveConstraintDescriptions(Class<?> javaBaseClass,
            String javaFieldName) {
        List<String> descriptions = new ArrayList<>();
        descriptions.addAll(constraintReader.getConstraintMessages(javaBaseClass, javaFieldName));

        // fallback to field itself if we got a getter and no annotation on it
        if (descriptions.isEmpty() && isGetter(javaFieldName)) {
            descriptions.addAll(constraintReader
                    .getConstraintMessages(javaBaseClass, fromGetter(javaFieldName)));
        }

        return descriptions;
    }
}
