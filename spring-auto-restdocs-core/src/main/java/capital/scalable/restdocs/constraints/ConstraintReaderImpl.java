/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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
package capital.scalable.restdocs.constraints;

import static capital.scalable.restdocs.constraints.ConstraintAndGroupDescriptionResolver.VALUE;
import static capital.scalable.restdocs.constraints.MethodParameterValidatorConstraintResolver.CONSTRAINT_CLASS;
import static capital.scalable.restdocs.util.FormatUtil.collectionToString;
import static capital.scalable.restdocs.util.TypeUtil.firstGenericType;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.ReflectionUtils.findField;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ConstraintDescriptions;

public class ConstraintReaderImpl implements ConstraintReader {

    private static final Logger log = getLogger(ConstraintReaderImpl.class);

    private final ConstraintAndGroupDescriptionResolver constraintAndGroupDescriptionResolver;

    private final SkippableConstraintResolver skippableConstraintResolver;

    private final MethodParameterConstraintResolver constraintResolver;

    private final ObjectMapper objectMapper;

    private final SnippetTranslationResolver translationResolver;

    private ConstraintReaderImpl(MethodParameterConstraintResolver actualResolver, ObjectMapper objectMapper, SnippetTranslationResolver translationResolver,
            ConstraintDescriptionResolver constraintDescriptionResolver) {
        this.translationResolver = translationResolver;
        constraintAndGroupDescriptionResolver = new ConstraintAndGroupDescriptionResolver(
                constraintDescriptionResolver, translationResolver);
        skippableConstraintResolver = new SkippableConstraintResolver(
                actualResolver, constraintAndGroupDescriptionResolver);
        constraintResolver = new HumanReadableConstraintResolver(skippableConstraintResolver);
        this.objectMapper = objectMapper;
    }

    public static ConstraintReaderImpl create(ObjectMapper objectMapper, SnippetTranslationResolver translationResolver, ConstraintDescriptionResolver constraintDescriptionResolver) {
        return CONSTRAINT_CLASS != null ? createWithValidation(objectMapper, translationResolver, constraintDescriptionResolver) : createWithoutValidation(objectMapper, translationResolver, constraintDescriptionResolver);
    }

    static ConstraintReaderImpl createWithoutValidation(ObjectMapper objectMapper, SnippetTranslationResolver translationResolver, ConstraintDescriptionResolver constraintDescriptionResolver) {
        return new ConstraintReaderImpl(new NoOpMethodParameterConstraintResolver(), objectMapper, translationResolver, constraintDescriptionResolver);
    }

    static ConstraintReaderImpl createWithValidation(ObjectMapper objectMapper, SnippetTranslationResolver translationResolver, ConstraintDescriptionResolver constraintDescriptionResolver) {
        return new ConstraintReaderImpl(new MethodParameterValidatorConstraintResolver(), objectMapper, translationResolver, constraintDescriptionResolver);
    }

    @Override
    public List<String> getOptionalMessages(Class<?> javaBaseClass, String javaFieldName) {
        return skippableConstraintResolver.getOptionalMessages(javaFieldName, javaBaseClass);
    }

    @Override
    public String getTypeSpecifier(Class<?> javaBaseClass) {
        String message = constraintAndGroupDescriptionResolver.resolveDescription(
                new Constraint(javaBaseClass.getCanonicalName(), emptyMap()));

        // fallback
        if (message.equals(javaBaseClass.getCanonicalName())) {
            return "";
        }

        return message;
    }

    @Override
    public List<String> getConstraintMessages(Class<?> javaBaseClass, String javaFieldName) {
        ConstraintDescriptions constraints = new ConstraintDescriptions(javaBaseClass,
                constraintResolver, constraintAndGroupDescriptionResolver);
        List<String> constraintMessages = new ArrayList<>();
        constraintMessages.addAll(constraints.descriptionsForProperty(javaFieldName));
        constraintMessages.addAll(getEnumConstraintMessage(javaBaseClass, javaFieldName));
        return constraintMessages;
    }

    @Override
    public List<String> getConstraintMessages(MethodParameter param) {
        List<Constraint> constraints = constraintResolver.resolveForParameter(param);
        List<String> constraintMessages = new ArrayList<>();
        for (Constraint constraint : constraints) {
            constraintMessages.add(
                    constraintAndGroupDescriptionResolver.resolveDescription(constraint));
        }
        constraintMessages.addAll(getEnumConstraintMessage(param));
        Collections.sort(constraintMessages);
        return constraintMessages;
    }

    private List<String> getEnumConstraintMessage(Class<?> javaBaseClass, String javaFieldName) {
        // could be getter actually
        Field field = findField(javaBaseClass, javaFieldName);
        if (field == null) {
            return emptyList();
        }

        if (field.getType().isEnum()) {
            return getEnumConstraintMessage(field.getType());
        } else if (field.getType().isArray()) {
            return getEnumConstraintMessage(field.getType().getComponentType());
        } else {
            return getEnumConstraintMessage(firstGenericType(field.getGenericType(), javaBaseClass));
        }
    }

    private List<String> getEnumConstraintMessage(MethodParameter param) {
        if (param.getParameterType().isEnum()) {
            return getEnumConstraintMessage(param.getParameterType());
        } else if (param.getParameterType().isArray()) {
            return getEnumConstraintMessage(param.getParameterType().getComponentType());
        } else {
            return getEnumConstraintMessage(firstGenericType(param));
        }
    }

    private List<String> getEnumConstraintMessage(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return getEnumConstraintMessage(clazz);
        } else {
            return emptyList();
        }
    }

    private List<String> getEnumConstraintMessage(Class<?> rawClass) {
        if (!rawClass.isEnum()) {
            return emptyList();
        }

        Class<Enum> enumClass = (Class<Enum>) rawClass;
        List<String> serializedEnumValues = new ArrayList<>();
        for (Enum e : enumClass.getEnumConstants()) {
            try {
                String jsonValue = objectMapper.writeValueAsString(e);
                // Result is wrapped in double quotes
                jsonValue = StringUtils.removeStart(jsonValue, "\"");
                jsonValue = StringUtils.removeEnd(jsonValue, "\"");
                serializedEnumValues.add(jsonValue);
            } catch (JsonProcessingException ex) {
                log.error("Failed to convert enum {}", e, ex);
            }
        }

        String value = collectionToString(serializedEnumValues);
        String enumName = enumClass.getCanonicalName();
        String message = constraintAndGroupDescriptionResolver.resolveDescription(
                new Constraint(enumName, singletonMap(VALUE, (Object) value)));

        // fallback
        if (isBlank(message) || message.equals(enumName)) {
            message = translationResolver.translate("constraints-enum", value);
        }
        return singletonList(message);
    }
}
