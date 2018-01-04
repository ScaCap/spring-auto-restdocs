/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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
import static capital.scalable.restdocs.constraints.ConstraintAndGroupDescriptionResolver.VALUE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
    public void noGroupDescriptionIsResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it"));
    }

    @Test
    public void noDescriptionIsNotResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint)))
                .thenThrow(MissingResourceException.class);
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Constraint"));
    }

    @Test
    public void noDescriptionWithGroupsIsResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{Update.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint)))
                .thenThrow(MissingResourceException.class);
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Constraint (groups: [Update])"));
    }

    @Test
    public void singleGroupDescriptionIsResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{Update.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint)))).thenReturn("Must be it (update)");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it (update)"));
    }

    @Test
    public void multipleGroupDescriptionsAreResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{Update.class, Create.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint))))
                .thenReturn("Must be it (create)", "Must be it (update)");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it (create), Must be it (update)"));
    }

    @Test
    public void groupDescriptionIsNotResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{Update.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint))))
                .thenThrow(MissingResourceException.class);
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it (groups: [Update])"));
    }

    @Test
    public void strangeGroupConfigurationIsResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, "no group");
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("Must be it");
        when(delegate.resolveDescription(not(eq(constraint)))).thenReturn("here");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Must be it"));
    }

    @Test
    public void noConstraintDescriptionIsResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{Update.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        when(delegate.resolveDescription(eq(constraint))).thenReturn("");
        // when
        String description = resolver.resolveDescription(constraint);
        // then
        assertThat(description, is("Constraint (groups: [Update])"));
    }

    @Test
    public void groupDescriptionResolved() {
        // given
        ArgumentCaptor<Constraint> captor = ArgumentCaptor.forClass(Constraint.class);
        when(delegate.resolveDescription(captor.capture())).thenReturn("Must be it (create)");
        // when
        String description = resolver.resolveGroupDescription(Create.class, "Must be it");
        // then
        assertThat(description, is("Must be it (create)"));
        assertThat(captor.getValue().getName(), is(Create.class.getName()));
        assertThat((String) captor.getValue().getConfiguration().get(VALUE), is("Must be it"));
    }

    @Test
    public void groupDescriptionNotResolved() {
        // given
        ArgumentCaptor<Constraint> captor = ArgumentCaptor.forClass(Constraint.class);
        when(delegate.resolveDescription(captor.capture())).thenReturn("");
        // when
        String description = resolver.resolveGroupDescription(Create.class, "Must be it");
        // then
        assertThat(description, is("Must be it (groups: [Create])"));
        assertThat(captor.getValue().getName(), is(Create.class.getName()));
        assertThat((String) captor.getValue().getConfiguration().get(VALUE), is("Must be it"));
    }

    @Test
    public void noGroupsResolved() {
        // given
        Constraint constraint =
                new Constraint("Constraint", Collections.<String, Object>emptyMap());
        // when
        List<Class<?>> groups = resolver.getGroups(constraint);
        // then
        assertThat(groups.size(), is(0));
    }

    @Test
    public void groupsResolved() {
        // given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(GROUPS, new Class<?>[]{Update.class, Create.class});
        Constraint constraint = new Constraint("Constraint", configuration);
        // when
        List<Class<?>> groups = resolver.getGroups(constraint);
        // then
        assertThat(groups.size(), is(2));
        assertThat(groups.get(0), equalTo((Class) Update.class));
        assertThat(groups.get(1), equalTo((Class) Create.class));
    }
}
