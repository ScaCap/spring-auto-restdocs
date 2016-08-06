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

package capital.scalable.restdocs.jackson.jackson;

import static capital.scalable.restdocs.jackson.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.jackson.util.FieldUtil.fromGetter;
import static capital.scalable.restdocs.jackson.util.FieldUtil.isGetter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import capital.scalable.restdocs.jackson.constraints.ConstraintReader;
import capital.scalable.restdocs.jackson.javadoc.JavadocReader;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;

public class FieldDocumentationVisitorContext {
    private final Set<Class<?>> analyzedClasses = new HashSet<>();
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

    public void addField(InternalFieldInfo info, Object jsonFieldType) {
        Class<?> javaFieldClass = info.getJavaBaseClass();
        String javaFieldName = info.getJavaFieldName();

        String comment = resolveComment(javaFieldClass, javaFieldName);

        FieldDescriptor fieldDescriptor = fieldWithPath(info.getJsonFieldPath())
                .type(jsonFieldType)
                .description(comment);

        if (isOptional(info)) {
            fieldDescriptor.optional();
        }

        Attribute constraints = constraintAttribute(info.getJavaBaseClass(),
                info.getJavaFieldName());
        fieldDescriptor.attributes(constraints);

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

    private boolean isOptional(InternalFieldInfo info) {
        for (Annotation annotation : info.getAnnotations()) {
            if (constraintReader.isMandatory(annotation.annotationType())) {
                return false;
            }
        }
        return true;
    }

    private Attribute constraintAttribute(Class<?> javaBaseClass, String javaFieldName) {
        return new Attribute(CONSTRAINTS_ATTRIBUTE, resolveConstraintDescriptions(
                javaBaseClass, javaFieldName));
    }

    private List<String> resolveConstraintDescriptions(Class<?> javaBaseClass,
            String javaFieldName) {
        List<String> descriptions = constraintReader
                .getConstraintMessages(javaBaseClass, javaFieldName);

        // fallback to field itself if we got a getter and no annotation on it
        if (descriptions.isEmpty() && isGetter(javaFieldName)) {
            descriptions = resolveConstraintDescriptions(javaBaseClass, fromGetter(javaFieldName));
        }

        return descriptions;
    }

    public void addAnalyzedClass(Class<?> clazz) {
        analyzedClasses.add(clazz);
    }

    public boolean wasAnalyzed(Class<?> clazz) {
        return analyzedClasses.contains(clazz);
    }
}
