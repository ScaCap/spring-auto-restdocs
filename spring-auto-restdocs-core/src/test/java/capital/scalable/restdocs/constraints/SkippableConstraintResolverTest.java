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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;

public class SkippableConstraintResolverTest {

    private static final Class<?> CLAZZ = Object.class;
    private static final String PROPERTY = "prop";

    private static final List<Constraint> CONSTRAINTS = asList(
            new Constraint(NotNull.class.getName(), null),
            new Constraint(Size.class.getName(), null),
            new Constraint(NotEmpty.class.getName(), null),
            new Constraint(Length.class.getName(), null),
            new Constraint(NotBlank.class.getName(), null));

    private SkippableConstraintResolver resolver;
    private MethodParameterConstraintResolver delegate;
    private GroupDescriptionResolver descriptionResolver;

    @Before
    public void setup() {
        delegate = mock(MethodParameterConstraintResolver.class);
        descriptionResolver = mock(GroupDescriptionResolver.class);
        resolver = new SkippableConstraintResolver(delegate, descriptionResolver);
    }

    @Test
    public void testNoConstraints() {
        // setup
        MethodParameter param = mock(MethodParameter.class);
        when(delegate.resolveForProperty(PROPERTY, CLAZZ))
                .thenReturn(new ArrayList<Constraint>());
        when(delegate.resolveForParameter(param))
                .thenReturn(new ArrayList<Constraint>());

        // when/then
        List<Constraint> constraints = resolver.resolveForProperty(PROPERTY, CLAZZ);
        assertThat(constraints.size(), is(0));

        // when/then
        constraints = resolver.resolveForParameter(param);
        assertThat(constraints.size(), is(0));

        // when/then
        List<String> messages = resolver.getOptionalMessages(PROPERTY, CLAZZ);
        assertThat(messages.size(), is(0));
    }

    @Test
    public void testSkippableConstraints() {
        // setup
        MethodParameter param = mock(MethodParameter.class);
        when(delegate.resolveForProperty(PROPERTY, CLAZZ))
                .thenReturn(CONSTRAINTS);
        when(delegate.resolveForParameter(param))
                .thenReturn(CONSTRAINTS);

        // when/then
        List<Constraint> constraints = resolver.resolveForProperty(PROPERTY, CLAZZ);
        assertConstraints(constraints);

        // when/then
        constraints = resolver.resolveForParameter(param);
        assertConstraints(constraints);

        // when/then
        List<String> messages = resolver.getOptionalMessages(PROPERTY, CLAZZ);
        assertThat(messages, is(singletonList("false")));
    }

    @Test
    public void testOptionalMessagesConstraintGroupsNotApplicable() {
        // setup
        Constraint constraint = new Constraint(Size.class.getName(), null);
        when(delegate.resolveForProperty(PROPERTY, CLAZZ))
                .thenReturn(singletonList(constraint));

        // when/then
        List<String> messages = resolver.getOptionalMessages(PROPERTY, CLAZZ);
        assertThat(messages.size(), is(0));

        verify(descriptionResolver, never()).getGroups(constraint);
    }

    @Test
    public void testOptionalMessagesNoConstraintGroups() {
        // setup
        Constraint constraint = new Constraint(NotNull.class.getName(), null);
        when(delegate.resolveForProperty(PROPERTY, CLAZZ))
                .thenReturn(singletonList(constraint));
        when(descriptionResolver.getGroups(constraint))
                .thenReturn(new ArrayList<Class<?>>());

        // when/then
        List<String> messages = resolver.getOptionalMessages(PROPERTY, CLAZZ);
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));
    }

    @Test
    public void testOptionalMessagesWithConstraintGroups() {
        // setup
        Constraint constraint = new Constraint(NotNull.class.getName(), null);
        when(delegate.resolveForProperty(PROPERTY, CLAZZ))
                .thenReturn(singletonList(constraint));
        when(descriptionResolver.getGroups(constraint))
                .thenReturn(Arrays.asList(GroupA.class, GroupB.class));
        when(descriptionResolver.resolveGroupDescription(GroupA.class, "false"))
                .thenReturn("group-A desc");
        when(descriptionResolver.resolveGroupDescription(GroupB.class, "false"))
                .thenReturn("group-B desc");

        // when/then
        List<String> messages = resolver.getOptionalMessages(PROPERTY, CLAZZ);
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0), is("group-A desc"));
        assertThat(messages.get(1), is("group-B desc"));
    }

    private void assertConstraints(List<Constraint> constraints) {
        assertThat(constraints.size(), is(2));
        assertThat(constraints.get(0).getName(), is(Size.class.getName()));
        assertThat(constraints.get(1).getName(), is(Length.class.getName()));
    }

    private interface GroupA {
    }

    private interface GroupB {
    }
}
