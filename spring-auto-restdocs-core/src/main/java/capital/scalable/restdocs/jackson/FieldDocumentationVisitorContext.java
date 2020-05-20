/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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
package capital.scalable.restdocs.jackson;

import static capital.scalable.restdocs.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.DEPRECATED_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.TYPE_ATTRIBUTE;
import static capital.scalable.restdocs.util.FieldUtil.fromGetter;
import static capital.scalable.restdocs.util.FieldUtil.isGetter;
import static capital.scalable.restdocs.util.FormatUtil.addDot;
import static capital.scalable.restdocs.util.FormatUtil.join;
import static capital.scalable.restdocs.util.TypeUtil.isPrimitive;
import static capital.scalable.restdocs.util.TypeUtil.resolveAnnotation;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;

class FieldDocumentationVisitorContext {
    private static final Logger log = getLogger(FieldDocumentationVisitorContext.class);

    private final List<FieldDescriptor> fields = new ArrayList<>();
    private final JavadocReader javadocReader;
    private final ConstraintReader constraintReader;
    private final DeserializationConfig deserializationConfig;
    private final SnippetTranslationResolver translationResolver;

    public FieldDocumentationVisitorContext(JavadocReader javadocReader,
            ConstraintReader constraintReader,
            DeserializationConfig deserializationConfig,
                                            SnippetTranslationResolver translationResolver) {
        this.javadocReader = javadocReader;
        this.constraintReader = constraintReader;
        this.deserializationConfig = deserializationConfig;
        this.translationResolver =translationResolver;
    }

    public List<FieldDescriptor> getFields() {
        return fields;
    }

    public void addField(InternalFieldInfo info, String jsonType) {
        String jsonFieldPath = info.getJsonFieldPath();
        String javaFieldTypeName = info.getJavaFieldType().getRawClass().getSimpleName();

        Class<?> javaBaseClass = info.getJavaBaseClass();
        String javaFieldName = info.getJavaFieldName();
        String comment = resolveComment(javaBaseClass, javaFieldName);
        comment = join("<br>", comment, resolveSeeTag(javaBaseClass, javaFieldName));

        List<String> constraints = constraintAttribute(javaBaseClass, javaFieldName);
        List<String> optionals = optionalAttribute(javaBaseClass, javaFieldName, info.isRequired());
        DeprecatedAttribute deprecated = deprecatedAttribute(javaBaseClass, javaFieldName);

        // in case of repeated field in subclass or subtype, let's just add info to already existing field descriptor
        FieldDescriptor descriptor = getOrCreate(jsonFieldPath, jsonType, javaFieldTypeName);

        if (javaBaseClass.equals(descriptor.getAttributes().get(TYPE_ATTRIBUTE))) {
            return; // exactly the same field, skip
        }

        String updatedComment = joinWithTypeSpecifier(javaBaseClass,
                (String) descriptor.getDescription(), comment);
        List<String> updatedConstraints = joinWithTypeSpecifier(javaBaseClass,
                (List<String>) descriptor.getAttributes().get(CONSTRAINTS_ATTRIBUTE), constraints);
        List<String> updatedOptionals = joinWithTypeSpecifier(javaBaseClass,
                (List<String>) descriptor.getAttributes().get(OPTIONAL_ATTRIBUTE), optionals);
        DeprecatedAttribute updatedDeprecated = joinWithTypeSpecifier(javaBaseClass,
                (DeprecatedAttribute) descriptor.getAttributes().get(DEPRECATED_ATTRIBUTE), deprecated);

        descriptor
                .description(updatedComment)
                .attributes(
                        new Attribute(TYPE_ATTRIBUTE, javaBaseClass),
                        new Attribute(CONSTRAINTS_ATTRIBUTE, updatedConstraints),
                        new Attribute(OPTIONAL_ATTRIBUTE, updatedOptionals),
                        new Attribute(DEPRECATED_ATTRIBUTE, updatedDeprecated));

        if (!fields.contains(descriptor.getPath())) {
            fields.add(descriptor);
        }

        log.debug("({}) {} added", jsonFieldPath, javaFieldTypeName);
    }

    private String joinWithTypeSpecifier(Class<?> javaBaseClass, String oldComment, String newComment) {
        if (isBlank(newComment)) {
            return trimToEmpty(oldComment);
        }
        String typeSpecifier = typeSpecifier(javaBaseClass);
        if (isNotBlank(typeSpecifier)) {
            typeSpecifier = " " + typeSpecifier;
        }
        return join("<br>", trimToEmpty(oldComment), newComment + typeSpecifier);
    }

    private DeprecatedAttribute joinWithTypeSpecifier(Class<?> javaBaseClass, DeprecatedAttribute oldAttr,
            DeprecatedAttribute newAttr) {
        if (!newAttr.isDeprecated()) {
            return oldAttr;
        }
        String typeSpecifier = typeSpecifier(javaBaseClass);
        if (isNotBlank(typeSpecifier)) {
            typeSpecifier = " " + typeSpecifier;
        }
        List<String> nl = new ArrayList<>();
        if (oldAttr != null) {
            nl.addAll(oldAttr.getValues());
        }
        nl.addAll(newAttr.getValues());
        return new DeprecatedAttribute(true, nl);
    }

    private List<String> joinWithTypeSpecifier(Class<?> javaBaseClass, List<String> oldTexts, List<String> newTexts) {
        List<String> result = new ArrayList<>();
        if (oldTexts != null) {
            result.addAll(oldTexts);
        }
        String typeSpecifier = typeSpecifier(javaBaseClass);
        if (isNotBlank(typeSpecifier)) {
            typeSpecifier = " " + typeSpecifier;
        }

        for (String newText : newTexts) {
            result.add(newText + typeSpecifier);
        }
        return result;
    }

    private String typeSpecifier(Class<?> javaBaseClass) {
        return trimToEmpty(constraintReader.getTypeSpecifier(javaBaseClass));
    }

    private FieldDescriptor getOrCreate(String jsonFieldPath, String jsonType, String javaFieldTypeName) {
        log.trace(" = WAS ADDED? {}", jsonFieldPath);
        for (FieldDescriptor descriptor : fields) {
            if (jsonFieldPath.equals(descriptor.getPath())) {
                log.trace("   = YES {}", descriptor.getPath());
                log.debug("({}) {} NOT added", jsonFieldPath, javaFieldTypeName);
                if (!descriptor.getType().equals(jsonType)) {
                    log.warn("Multiple fields with the same path {} but different types!", jsonFieldPath);
                }
                return descriptor;
            }
            log.trace("   = NO {}", descriptor.getPath());
        }
        return fieldWithPath(jsonFieldPath)
                .type(jsonType);
    }

    private List<String> constraintAttribute(Class<?> javaBaseClass, String javaFieldName) {
        return resolveConstraintDescriptions(javaBaseClass, javaFieldName);
    }

    private List<String> optionalAttribute(Class<?> javaBaseClass, String javaFieldName, boolean requiredField) {
        return resolveOptionalMessages(javaBaseClass, javaFieldName, requiredField);
    }

    private DeprecatedAttribute deprecatedAttribute(Class<?> javaBaseClass, String javaFieldName) {
        return resolveDeprecatedMessage(javaBaseClass, javaFieldName);
    }

    private List<String> resolveOptionalMessages(Class<?> javaBaseClass, String javaFieldName, boolean requiredField) {
        List<String> optionalMessages = new ArrayList<>();

        if (requiredField) {
            optionalMessages.add("false");
            return optionalMessages;
        }

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

    private DeprecatedAttribute resolveDeprecatedMessage(Class<?> javaBaseClass, String javaFieldName) {
        boolean isDeprecated =
                resolveAnnotation(javaBaseClass, javaFieldName, Deprecated.class) != null;
        String comment = resolveTag(javaBaseClass, javaFieldName, "deprecated");
        if (isDeprecated || isNotBlank(comment)) {
            return new DeprecatedAttribute(true, singletonList(trimToEmpty(comment)));
        } else {
            return new DeprecatedAttribute(false, emptyList());
        }
    }

    private String resolveSeeTag(Class<?> javaBaseClass, String javaFieldName) {
        String comment = resolveTag(javaBaseClass, javaFieldName, "see");
        if (isNotBlank(comment)) {
            return addDot(translationResolver.translate("tags-see", comment));
        } else {
            return "";
        }
    }

    private String resolveComment(Class<?> javaBaseClass, String javaFieldOrMethodName) {
        // prefer method comments first
        String comment = javadocReader.resolveMethodComment(javaBaseClass, javaFieldOrMethodName);
        if (isBlank(comment)) {
            // fallback to field
            comment = javadocReader.resolveFieldComment(javaBaseClass, javaFieldOrMethodName);
        }
        if (isBlank(comment) && isGetter(javaFieldOrMethodName)) {
            // fallback if name is getter method but comment is on field itself
            comment = javadocReader.resolveFieldComment(javaBaseClass,
                    fromGetter(javaFieldOrMethodName));
        }
        return comment;
    }

    private String resolveTag(Class<?> javaBaseClass, String javaFieldOrMethodName,
            String tagName) {
        // prefer method comments first
        String comment = javadocReader.resolveMethodTag(javaBaseClass, javaFieldOrMethodName,
                tagName);
        if (isBlank(comment)) {
            // fallback to field
            comment = javadocReader.resolveFieldTag(javaBaseClass, javaFieldOrMethodName, tagName);
        }
        if (isBlank(comment) && isGetter(javaFieldOrMethodName)) {
            // fallback if name is getter method but comment is on field itself
            comment = javadocReader.resolveFieldTag(javaBaseClass,
                    fromGetter(javaFieldOrMethodName), tagName);
        }
        return comment;
    }
}
