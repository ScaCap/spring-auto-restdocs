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

import static capital.scalable.restdocs.constraints.SkippableConstraintResolver
        .MANDATORY_VALUE_ANNOTATIONS;
import static org.apache.commons.lang3.ArrayUtils.contains;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.constraints.ConstraintResolver;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ValidatorConstraintResolver;

public class ConstraintReaderImpl implements ConstraintReader {
    private ConstraintAndGroupDescriptionResolver constraintDescriptionResolver =
            new ConstraintAndGroupDescriptionResolver(
                    new ResourceBundleConstraintDescriptionResolver());

    private final SkippableConstraintResolver skippableConstraintResolver =
            new SkippableConstraintResolver(new ValidatorConstraintResolver(),
                    constraintDescriptionResolver);

    private ConstraintResolver constraintResolver =
            new HumanReadableConstraintResolver(skippableConstraintResolver);

    @Override
    public List<String> getOptionalMessages(Class<?> javaBaseClass, String javaFieldName) {
        return skippableConstraintResolver.getOptionalMessages(javaFieldName, javaBaseClass);
    }

    @Override
    public boolean isMandatory(Class<?> annotation) {
        return contains(MANDATORY_VALUE_ANNOTATIONS, annotation);
    }

    @Override
    public List<String> getConstraintMessages(Class<?> javaBaseClass, String javaFieldName) {
        ConstraintDescriptions constraints = new ConstraintDescriptions(javaBaseClass,
                constraintResolver, constraintDescriptionResolver);
        return constraints.descriptionsForProperty(javaFieldName);
    }

    @Override
    public List<String> getConstraintMessages(MethodParameter param) {
        return new ArrayList<>(); // TODO method parameter constraints
    }
}
