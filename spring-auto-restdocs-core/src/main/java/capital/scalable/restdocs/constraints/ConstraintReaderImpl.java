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

package capital.scalable.restdocs.constraints;

import static capital.scalable.restdocs.constraints.ConstraintAndGroupDescriptionResolver.VALUE;
import static capital.scalable.restdocs.constraints.MethodParameterValidatorConstraintResolver
        .CONSTRAINT_CLASS;
import static capital.scalable.restdocs.i18n.SnippetTranslationResolver.translate;
import static capital.scalable.restdocs.util.FormatUtil.arrayToString;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ReflectionUtils.findField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;

public class ConstraintReaderImpl implements ConstraintReader {

    private final ConstraintAndGroupDescriptionResolver constraintDescriptionResolver;

    private final SkippableConstraintResolver skippableConstraintResolver;

    private final MethodParameterConstraintResolver constraintResolver;

    private ConstraintReaderImpl(MethodParameterConstraintResolver actualResolver) {
        constraintDescriptionResolver = new ConstraintAndGroupDescriptionResolver(
                new ResourceBundleConstraintDescriptionResolver());
        skippableConstraintResolver = new SkippableConstraintResolver(
                actualResolver, constraintDescriptionResolver);
        constraintResolver = new HumanReadableConstraintResolver(skippableConstraintResolver);
    }

    public static ConstraintReaderImpl create() {
        return CONSTRAINT_CLASS != null ? createWithValidation() : createWithoutValidation();
    }

    static ConstraintReaderImpl createWithoutValidation() {
        return new ConstraintReaderImpl(new NoOpMethodParameterConstraintResolver());
    }

    static ConstraintReaderImpl createWithValidation() {
        return new ConstraintReaderImpl(new MethodParameterValidatorConstraintResolver());
    }

    @Override
    public List<String> getOptionalMessages(Class<?> javaBaseClass, String javaFieldName) {
        return skippableConstraintResolver.getOptionalMessages(javaFieldName, javaBaseClass);
    }

    @Override
    public List<String> getConstraintMessages(Class<?> javaBaseClass, String javaFieldName) {
        ConstraintDescriptions constraints = new ConstraintDescriptions(javaBaseClass,
                constraintResolver, constraintDescriptionResolver);
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
                    constraintDescriptionResolver.resolveDescription(constraint));
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

        return getEnumConstraintMessage(field.getType());
    }

    private List<String> getEnumConstraintMessage(MethodParameter param) {
        return getEnumConstraintMessage(param.getParameterType());
    }

    private List<String> getEnumConstraintMessage(Class<?> rawClass) {
        if (!rawClass.isEnum()) {
            return emptyList();
        }

        Class<Enum> enumClass = (Class<Enum>) rawClass;

        String value = arrayToString(enumClass.getEnumConstants());
        String enumName = enumClass.getCanonicalName();
        String message = constraintDescriptionResolver.resolveDescription(
                new Constraint(enumName, singletonMap(VALUE, (Object) value)));

        // fallback
        if (isBlank(message) || message.equals(enumName)) {
            message = translate("constraints-enum", value);
        }
        return singletonList(message);
    }
}
