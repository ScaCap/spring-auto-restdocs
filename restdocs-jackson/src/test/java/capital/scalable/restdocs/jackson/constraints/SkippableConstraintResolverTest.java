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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

public class SkippableConstraintResolverTest {

    private static final String NOTNULL_NAME = NotNull.class.getCanonicalName();
    private static final String SIZE_NAME = Size.class.getCanonicalName();
    private static final String NOTEMPTY_NAME = NotEmpty.class.getCanonicalName();

    private static final String PROPERTY = "prop";
    private static final Class<?> CLAZZ = Object.class;
    private static final List<Constraint> CONSTRAINTS = asList(
            new Constraint(NOTNULL_NAME, null),
            new Constraint(SIZE_NAME, null),
            new Constraint(NOTEMPTY_NAME, null));

    private SkippableConstraintResolver resolver;

    private ConstraintResolver delegate;

    @Before
    public void setup() {
        delegate = mock(ConstraintResolver.class);
        when(delegate.resolveForProperty(PROPERTY, CLAZZ))
                .thenReturn(CONSTRAINTS);
    }

    @Test
    public void testNoSkippableConstraints() {
        resolver = new SkippableConstraintResolver(delegate);

        List<Constraint> constraints = resolver.resolveForProperty(PROPERTY, CLAZZ);
        assertThat(constraints.size(), is(3));
        assertThat(constraints.get(0).getName(), is(NOTNULL_NAME));
        assertThat(constraints.get(1).getName(), is(SIZE_NAME));
        assertThat(constraints.get(2).getName(), is(NOTEMPTY_NAME));
    }

    @Test
    public void testSkippableConstraints() {
        resolver = new SkippableConstraintResolver(delegate, NotNull.class, NotEmpty.class);

        List<Constraint> constraints = resolver.resolveForProperty(PROPERTY, CLAZZ);
        assertThat(constraints.size(), is(1));
        assertThat(constraints.get(0).getName(), is(SIZE_NAME));
    }
}