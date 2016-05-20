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

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.util.StringUtils.hasLength;

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
        String fieldComment = javadocReader
                .resolveFieldComment(info.getJavaBaseClass(), info.getJavaFieldName());
        // fallback if field is getter
        String methodComment = javadocReader
                .resolveMethodComment(info.getJavaBaseClass(), info.getJavaFieldName());

        String comment = hasLength(fieldComment) ? fieldComment
                : (hasLength(methodComment) ? methodComment : "");

        FieldDescriptor fieldDescriptor = fieldWithPath(info.getJsonFieldPath())
                .type(jsonFieldType)
                .description(comment);

        if (info.getOptional()) {
            fieldDescriptor.optional();
        }

        fields.add(fieldDescriptor);
    }

    public void addAnalyzedClass(Class<?> clazz) {
        analyzedClasses.add(clazz);
    }

    public boolean wasAnalyzed(Class<?> clazz) {
        return analyzedClasses.contains(clazz);
    }
}
