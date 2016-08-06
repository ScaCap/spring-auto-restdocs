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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;

public class ConstraintAndGroupDescriptionResolverTest {

    private ConstraintAndGroupDescriptionResolver resolver;

    private ConstraintDescriptionResolver delegate;

    @Before
    public void setup() {
        delegate = mock(ConstraintDescriptionResolver.class);
        resolver = new ConstraintAndGroupDescriptionResolver(delegate);
    }

    @Test
    public void noGroupDescriptionShouldBeResolved() {
        // given
        Map<String, Object> configuration = new HashedMap();
        configuration.put("groups", new Class<?>[]{});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint)))).thenReturn("here");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it"));
    }

    @Test
    public void singleGroupDescriptionShouldBeResolved() {
        // given
        Map<String, Object> configuration = new HashedMap();
        configuration.put("groups", new Class<?>[]{ExampleConstraintGroup.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint)))).thenReturn("here");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it (here)"));
    }

    @Test
    public void multipleGroupDescriptionShouldBeResolved() {
        // given
        Map<String, Object> configuration = new HashedMap();
        configuration.put("groups",
                new Class<?>[]{ExampleConstraintGroup.class, ExampleConstraintGroup.class,
                        ExampleConstraintGroup.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint)))).thenReturn("here");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it (here, here, here)"));
    }

    @Test
    public void groupWithoutDescriptionShouldBeResolved() {
        // given
        Map<String, Object> configuration = new HashedMap();
        configuration.put("groups",
                new Class<?>[]{ExampleConstraintGroup.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it"));
    }

    @Test
    public void strangeGroupConfigurationShouldBeResolved() {
        // given
        Map<String, Object> configuration = new HashedMap();
        configuration.put("groups", "no group");
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint)))).thenReturn("here");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it"));
    }

    @Test
    public void groupDescriptionButNoConstraintDescriptionShouldBeResolved() {
        // given
        Map<String, Object> configuration = new HashedMap();
        configuration.put("groups", new Class<?>[]{ExampleConstraintGroup.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("");
        when(delegate.resolveDescription(not(eq(constraint)))).thenReturn("here");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is(""));
    }
}
