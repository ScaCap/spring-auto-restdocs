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
package capital.scalable.restdocs.constraints;

import static capital.scalable.restdocs.util.FormatUtil.arrayToString;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;

class HumanReadableConstraintResolver implements MethodParameterConstraintResolver {
    private static final Logger log = getLogger(HumanReadableConstraintResolver.class);

    private static final String[] IGNORED_FIELDS = new String[]{"groups", "payload"};

    private final MethodParameterConstraintResolver delegate;

    private final Set<String> ignoredFields;

    public HumanReadableConstraintResolver(MethodParameterConstraintResolver delegate) {
        this.delegate = delegate;
        this.ignoredFields = new HashSet<>();
        Collections.addAll(this.ignoredFields, IGNORED_FIELDS);
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

    @Override
    public List<Constraint> resolveForParameter(MethodParameter param) {
        List<Constraint> result = new ArrayList<>();
        for (Constraint constraint : delegate.resolveForParameter(param)) {
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
            return arrayToString((Object[]) o);
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
