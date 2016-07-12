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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.util.StringUtils.uncapitalize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import capital.scalable.restdocs.jackson.javadoc.JavadocReader;
import org.springframework.restdocs.payload.FieldDescriptor;

/**
 * @author Florian Benz
 */
public class FieldDocumentationVisitorContext {

    private final Set<Class<?>> analyzedClasses = new HashSet<>();

    private final List<FieldDescriptor> fields = new ArrayList<>();
    private JavadocReader javadocReader;

    public FieldDocumentationVisitorContext(JavadocReader javadocReader) {
        this.javadocReader = javadocReader;
    }

    public List<FieldDescriptor> getFields() {
        return fields;
    }

    public void addField(InternalFieldInfo info, Object jsonFieldType) {
        Class<?> javaFieldClass = info.getJavaBaseClass();
        String javaFieldName = info.getJavaFieldName();

        String comment = javadocReader.resolveFieldComment(javaFieldClass, javaFieldName);
        if (isBlank(comment)) {
            // fallback if fieldName is getter method and comment is on the method itself
            comment = javadocReader.resolveMethodComment(javaFieldClass, javaFieldName);
        }
        if (isBlank(comment) && isGetter(javaFieldName)) {
            // fallback if fieldName is getter method but comment is on field itself
            comment = javadocReader.resolveFieldComment(javaFieldClass, fromGetter(javaFieldName));
        }

        if (!isBlank(info.getValidationInformation())) {
            comment = addValidationInformation(info, comment);
        }

        FieldDescriptor fieldDescriptor = fieldWithPath(info.getJsonFieldPath())
                .type(jsonFieldType)
                .description(comment);

        if (info.getOptional()) {
            fieldDescriptor.optional();
        }
        fields.add(fieldDescriptor);
    }

    private String addValidationInformation(InternalFieldInfo info, String comment) {
        if (isBlank(comment)) {
            comment = info.getValidationInformation();
        } else {
            comment = comment + "; " + info.getValidationInformation();
        }
        return comment;
    }

    private String fromGetter(String javaMethodName) {
        int cut = javaMethodName.startsWith("get") ? "get".length() : "is".length();
        return uncapitalize(javaMethodName.substring(cut, javaMethodName.length()));
    }

    private boolean isGetter(String javaFieldName) {
        return javaFieldName.startsWith("get") || javaFieldName.startsWith("is");
    }

    public void addAnalyzedClass(Class<?> clazz) {
        analyzedClasses.add(clazz);
    }

    public boolean wasAnalyzed(Class<?> clazz) {
        return analyzedClasses.contains(clazz);
    }
}
