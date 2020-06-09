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

import static capital.scalable.restdocs.constraints.ConstraintAndGroupDescriptionResolver.GROUPS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;

public class DynamicResourceBundleConstraintDescriptionResolverTest {

    private ConstraintDescriptionResolver resolver =
            new DynamicResourceBundleConstraintDescriptionResolver();

    @Test
    public void descriptionIsResolvedFromAnnotationMessage() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("message", "Must be it");
        Constraint constraint = new Constraint("test.Constraint1", configuration);
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it"));
    }

    @Test
    public void descriptionIsResolvedFromResource() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        Constraint constraint = new Constraint("test.Constraint2", configuration);
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be limited"));
    }

    @Test
    public void descriptionIsNotResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        Constraint constraint = new Constraint("test.Constraint3", configuration);
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is(""));
    }
}
