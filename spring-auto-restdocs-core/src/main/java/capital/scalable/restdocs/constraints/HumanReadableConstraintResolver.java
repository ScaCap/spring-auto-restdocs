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

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.arrayToDelimitedString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

class HumanReadableConstraintResolver implements ConstraintResolver {
    private static final Logger log = getLogger(HumanReadableConstraintResolver.class);

    private static final String[] IGNORED_FIELDS = new String[]{"groups", "payload"};

    private final ConstraintResolver delegate;

    private final Set<String> ignoredFields;

    public HumanReadableConstraintResolver(ConstraintResolver delegate) {
        this.delegate = delegate;
        this.ignoredFields = new HashSet<>();
        for (String field : IGNORED_FIELDS) {
            this.ignoredFields.add(field);
        }
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        List<Constraint> result = new ArrayList<>();
        for (Constraint constraint : delegate.resolveForProperty(property, clazz)) {
            result.add(new Constraint(constraint.getName(),
                    extendConfiguration(constraint.getConfiguration())));
        }
        return result;
    }

    private Map<String, Object> extendConfiguration(Map<String, Object> configuration) {
        Map<String, Object> extendedConfiguration = new HashMap<>();
        for (Map.Entry<String, Object> e : configuration.entrySet()) {
            if (ignoredFields.contains(e.getKey())) {
                extendedConfiguration.put(e.getKey(), e.getValue());
            } else {
                extendedConfiguration.put(e.getKey(), humanReadableString(e.getValue()));
            }
        }
        return extendedConfiguration;
    }

    private String humanReadableString(Object o) {
        if (o instanceof Object[]) {
            return "[" + arrayToDelimitedString((Object[]) o, ", ") + "]";
        } else if (o instanceof Class) {
            try {
                return ((Class) o).newInstance().toString();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to create an instance of {}", ((Class) o).getCanonicalName(), e);
                return "Failed to create an instance of " + ((Class) o).getCanonicalName() +
                        ". Does the class have a no args constructor?";
            }
        } else {
            return o.toString();
        }
    }
}
