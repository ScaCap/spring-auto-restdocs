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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;


class SkippableConstraintResolver implements MethodParameterConstraintResolver {
    public static final Collection<String> MANDATORY_VALUE_ANNOTATIONS = Arrays.asList(
            "javax.validation.constraints.NotNull",
            "org.hibernate.validator.constraints.NotBlank",
            "org.hibernate.validator.constraints.NotEmpty");

    private final MethodParameterConstraintResolver delegate;
    private final GroupDescriptionResolver descriptionResolver;

    public SkippableConstraintResolver(MethodParameterConstraintResolver delegate,
            GroupDescriptionResolver descriptionResolver) {
        this.delegate = delegate;
        this.descriptionResolver = descriptionResolver;
    }

    private boolean isSkippable(Constraint constraint) {
        return MANDATORY_VALUE_ANNOTATIONS.contains(constraint.getName());
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        List<Constraint> result = new ArrayList<>();
        for (Constraint constraint : delegate.resolveForProperty(property, clazz)) {
            if (isSkippable(constraint))
                continue;
            result.add(constraint);
        }
        return result;
    }

    @Override
    public List<Constraint> resolveForParameter(MethodParameter parameter) {
        List<Constraint> result = new ArrayList<>();
        for (Constraint constraint : delegate.resolveForParameter(parameter)) {
            if (isSkippable(constraint))
                continue;
            result.add(constraint);
        }
        return result;
    }

    public List<String> getOptionalMessages(String property, Class<?> clazz) {
        List<String> result = new ArrayList<>();
        List<Constraint> constraints = delegate.resolveForProperty(property, clazz);
        String defaultOptional = null;

        for (Constraint constraint : constraints) {
            if (isSkippable(constraint)) {
                List<Class<?>> groups = getGroups(constraint);

                if (groups.isEmpty()) {
                    defaultOptional = "false";
                } else {
                    for (Class group : groups) {
                        result.add(mandatoryForGroup(group));
                    }
                }
            }
        }

        Collections.sort(result);
        if (defaultOptional != null) {
            result.add(0, defaultOptional);
        }
        return result;
    }

    private List<Class<?>> getGroups(Constraint constraint) {
        return descriptionResolver.getGroups(constraint);
    }

    private String mandatoryForGroup(Class group) {
        return descriptionResolver.resolveGroupDescription(group, "false");
    }
}
