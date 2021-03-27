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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;

public class ConstraintAndGroupDescriptionResolver implements
        ConstraintDescriptionResolver, GroupDescriptionResolver {

    static final String GROUPS = "groups";
    static final String VALUE = "value";

    private final ConstraintDescriptionResolver delegate;
    private final SnippetTranslationResolver translationResolver;

    public ConstraintAndGroupDescriptionResolver(ConstraintDescriptionResolver delegate, SnippetTranslationResolver translationResolver) {
        this.delegate = delegate;
        this.translationResolver = translationResolver;
    }

    @Override
    public String resolveDescription(Constraint constraint) {
        String constraintDescription = trimToEmpty(resolvePlainDescription(constraint));
        if (isBlank(constraintDescription)) {
            constraintDescription = constraint.getName();
        }

        List<Class<?>> groups = getGroups(constraint);
        if (groups.isEmpty()) {
            return constraintDescription;
        }

        StringBuilder result = new StringBuilder();
        for (Class group : groups) {
            result.append(", ");
            result.append(trimToEmpty(resolveGroupDescription(group, constraintDescription)));
        }
        result.replace(0, 2, "");
        return result.toString();
    }

    @Override
    public List<Class<?>> getGroups(Constraint constraint) {
        Object rawGroups = constraint.getConfiguration().get(GROUPS);
        if (!(rawGroups instanceof Class[])) {
            return emptyList();
        }
        Class<?>[] groups = (Class[]) rawGroups;

        List<Class<?>> result = new ArrayList<>();
        Collections.addAll(result, groups);
        return result;
    }

    @Override
    public String resolveGroupDescription(Class group, String constraintDescription) {
        // Pretending that the group class is a constraint to use the same logic for getting
        // a description.
        Constraint groupConstraint = new Constraint(group.getCanonicalName(),
                singletonMap(VALUE, constraintDescription));
        String result = resolvePlainDescription(groupConstraint);
        return isBlank(result) ? fallbackGroupDescription(group, constraintDescription) : result;
    }

    private String fallbackGroupDescription(Class group, String constraintDescription) {
        return translationResolver.translate("constraints-groups", constraintDescription, group.getSimpleName());
    }

    private String resolvePlainDescription(Constraint constraint) {
        return delegate.resolveDescription(constraint);
    }
}
