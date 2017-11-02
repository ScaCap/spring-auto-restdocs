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
import static capital.scalable.restdocs.constraints.ConstraintReader.DEPRECATED_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static capital.scalable.restdocs.util.FieldUtil.fromGetter;
import static capital.scalable.restdocs.util.FieldUtil.isGetter;
import static capital.scalable.restdocs.util.TypeUtil.isPrimitive;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.util.ReflectionUtils;

class FieldDocumentationVisitorContext {
    private static final Logger log = getLogger(FieldDocumentationVisitorContext.class);

    private final List<FieldDescriptor> fields = new ArrayList<>();
    private final JavadocReader javadocReader;
    private final ConstraintReader constraintReader;
    private final DeserializationConfig deserializationConfig;

    public FieldDocumentationVisitorContext(JavadocReader javadocReader,
            ConstraintReader constraintReader,
            DeserializationConfig deserializationConfig) {
        this.javadocReader = javadocReader;
        this.constraintReader = constraintReader;
        this.deserializationConfig = deserializationConfig;
    }

    public List<FieldDescriptor> getFields() {
        return fields;
    }

    public void addField(InternalFieldInfo info, String jsonType) {
        String jsonFieldPath = info.getJsonFieldPath();
        String javaFieldTypeName = info.getJavaFieldType().getRawClass().getSimpleName();

        if (isPresent(jsonFieldPath, javaFieldTypeName)) {
            return; // do not add duplicates
        }

        Class<?> javaBaseClass = info.getJavaBaseClass();
        String javaFieldName = info.getJavaFieldName();
        String comment = resolveComment(javaBaseClass, javaFieldName);

        FieldDescriptor fieldDescriptor = fieldWithPath(jsonFieldPath)
                .type(jsonType)
                .description(comment);

        Attribute constraints = constraintAttribute(javaBaseClass, javaFieldName);
        Attribute optionals = optionalAttribute(javaBaseClass, javaFieldName);
        Attribute deprecated = deprecatedAttribute(javaBaseClass, javaFieldName);
        fieldDescriptor.attributes(constraints, optionals, deprecated);

        fields.add(fieldDescriptor);
        log.debug("({}) {} added", jsonFieldPath, javaFieldTypeName);
    }

    private boolean isPresent(String jsonFieldPath, String javaFieldTypeName) {
        log.trace(" = WAS ADDED? {}", jsonFieldPath);
        for (FieldDescriptor descriptor : fields) {
            if (descriptor.getPath().equals(jsonFieldPath)) {
                log.trace("   = YES {}", descriptor.getPath());
                log.debug("({}) {} NOT added", jsonFieldPath, javaFieldTypeName);
                return true;
            }
            log.trace("   = NO {}", descriptor.getPath());
        }
        return false;
    }

    private String resolveComment(Class<?> javaBaseClass, String javaFieldName) {
        String comment = javadocReader.resolveFieldComment(javaBaseClass, javaFieldName);
        if (isBlank(comment)) {
            // fallback if fieldName is getter method and comment is on the method itself
            comment = javadocReader.resolveMethodComment(javaBaseClass, javaFieldName);
        }
        if (isBlank(comment) && isGetter(javaFieldName)) {
            // fallback if fieldName is getter method but comment is on field itself
            comment = javadocReader.resolveFieldComment(javaBaseClass, fromGetter(javaFieldName));
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

    private Attribute deprecatedAttribute(Class<?> javaBaseClass, String javaFieldName) {
        return new Attribute(DEPRECATED_ATTRIBUTE,
                resolveDeprecatedMessage(javaBaseClass, javaFieldName));
    }

    private List<String> resolveOptionalMessages(Class<?> javaBaseClass, String javaFieldName) {
        List<String> optionalMessages = new ArrayList<>();

        if (isPrimitive(javaBaseClass, javaFieldName)) {
            boolean failOnNullForPrimitives = deserializationConfig.hasDeserializationFeatures(
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES.getMask());
            optionalMessages.add("" + !failOnNullForPrimitives);
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

    private String resolveDeprecatedMessage(Class<?> javaBaseClass, String javaFieldName) {
        Field field = ReflectionUtils.findField(javaBaseClass, javaFieldName);
        boolean isDeprecated = field != null && field.getAnnotation(Deprecated.class) != null;
        String comment = javadocReader.resolveFieldTag(javaBaseClass, javaFieldName, "deprecated");
        if (isDeprecated || isNotBlank(comment)) {
            return trimToEmpty(comment);
        } else {
            return null;
        }
    }
}
