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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

class SkippableConstraintResolver implements ConstraintResolver {
    private final ConstraintResolver delegate;
    private final Collection<String> skippableConstraints;

    public SkippableConstraintResolver(ConstraintResolver delegate,
            Class<?>... skippableConstraints) {
        this.delegate = delegate;
        this.skippableConstraints = new ArrayList<>();
        for (Class<?> a : skippableConstraints) {
            this.skippableConstraints.add(a.getCanonicalName());
        }
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

    private boolean isSkippable(Constraint constraint) {
        return skippableConstraints.contains(constraint.getName());
    }
}
