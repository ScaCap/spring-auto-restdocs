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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @Before
    public void setup() {
        delegate = mock(MethodParameterConstraintResolver.class);
        GroupDescriptionResolver descriptionResolver = mock(GroupDescriptionResolver.class);
        resolver = new SkippableConstraintResolver(delegate, descriptionResolver);
    }

    @Test
    public void testSkippableConstraints() {
        when(delegate.resolveForProperty(PROPERTY, CLAZZ))
                .thenReturn(CONSTRAINTS);
        when(delegate.resolveForParameter(any(MethodParameter.class)))
                .thenReturn(CONSTRAINTS);

        List<Constraint> constraints = resolver.resolveForProperty(PROPERTY, CLAZZ);
        assertConstraints(constraints);

        constraints = resolver.resolveForParameter(mock(MethodParameter.class));
        assertConstraints(constraints);
    }

    private void assertConstraints(List<Constraint> constraints) {
        assertThat(constraints.size(), is(2));
        assertThat(constraints.get(0).getName(), is(Size.class.getName()));
        assertThat(constraints.get(1).getName(), is(Length.class.getName()));
    }
}