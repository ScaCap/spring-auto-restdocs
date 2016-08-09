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

package capital.scalable.restdocs.jackson.constraints;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.collectionToDelimitedString;
import static org.springframework.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;

import org.slf4j.Logger;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;

public class ConstraintAndGroupDescriptionResolver implements
        ConstraintDescriptionResolver {
    private static final Logger log = getLogger(ConstraintAndGroupDescriptionResolver.class);

    private final ConstraintDescriptionResolver delegate;

    public ConstraintAndGroupDescriptionResolver(ConstraintDescriptionResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public String resolveDescription(Constraint constraint) {
        String constraintDescription = delegate.resolveDescription(constraint);
        if (!hasText(constraintDescription)) {
            return "";
        }
        List<String> groupDescriptions = groupDescriptions(constraint);
        if (groupDescriptions.isEmpty()) {
            return constraintDescription;
        } else {
            return constraintDescription + " (" +
                    collectionToDelimitedString(groupDescriptions, ", ") + ")";
        }
    }

    private List<String> groupDescriptions(Constraint constraint) {
        Object rawGroups = constraint.getConfiguration().get("groups");
        if (!(rawGroups instanceof Class[])) {
            return emptyList();
        }
        List<String> groupDescriptions = new ArrayList<>();
        Class[] groups = (Class[]) rawGroups;
        for (Class clazz : groups) {
            addGroupDescriptionIfPresent(groupDescriptions, clazz);
        }
        return groupDescriptions;
    }

    private void addGroupDescriptionIfPresent(List<String> groupDescriptions, Class clazz) {
        // Pretending that the group class is a constraint to use the same logic for getting
        // a description.
        try {
            String description = delegate.resolveDescription(
                    new Constraint(clazz.getName(), Collections.<String, Object>emptyMap()));
            if (hasText(description)) {
                groupDescriptions.add(description);
            }
        } catch (MissingResourceException e) {
            log.info("No description found for constraint group {}", clazz.getName(), e);
        }
    }
}
