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

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;

public class HumanReadableConstraintResolverTest {
    private HumanReadableConstraintResolver resolver;
    private MethodParameterConstraintResolver delegate;

    @Before
    public void setup() {
        delegate = mock(MethodParameterConstraintResolver.class);
        resolver = new HumanReadableConstraintResolver(delegate);
    }

    @Test
    public void testHumanReadable() {
        // setup
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("primitive", 1);
        configuration.put("wrapper", 1);
        configuration.put("object", new CustomObj("Peter"));
        configuration.put("array", new Object[]{"value1", "value2"});
        configuration.put("class", CustomConstraint.class);
        configuration.put("groups", new Class<?>[]{Update.class});
        configuration.put("payload", new Class<?>[0]);
        configuration.put("ref", Entity.class);
        Constraint constraint = new Constraint("Custom", configuration);

        when(delegate.resolveForProperty("prop", this.getClass()))
                .thenReturn(singletonList(constraint));
        when(delegate.resolveForParameter(any(MethodParameter.class)))
                .thenReturn(singletonList(constraint));

        List<Constraint> constraints = resolver.resolveForProperty("prop", this.getClass());
        assertConstraints(constraints);

        constraints = resolver.resolveForParameter(mock(MethodParameter.class));
        assertConstraints(constraints);
    }

    private void assertConstraints(List<Constraint> constraints) {
        assertThat(constraints.size(), is(1));
        assertThat(constraints.get(0).getConfiguration().size(), is(8));
        assertThat(constraints.get(0).getConfiguration().get("primitive").toString(), is("1"));
        assertThat(constraints.get(0).getConfiguration().get("wrapper").toString(), is("1"));
        assertThat(constraints.get(0).getConfiguration().get("object").toString(), is("I'm Peter"));
        assertThat(constraints.get(0).getConfiguration().get("array").toString(),
                is("[value1, value2]"));
        assertThat(constraints.get(0).getConfiguration().get("class").toString(),
                is("I'm custom constraint"));
        // groups and payload belong to the fields that is not touched
        assertThat(constraints.get(0).getConfiguration().get("groups"), instanceOf(Class[].class));
        assertThat(constraints.get(0).getConfiguration().get("payload"), instanceOf(Class[].class));
        assertThat(constraints.get(0).getConfiguration().get("ref").toString(), is("Entity"));
    }

    static class CustomObj {
        private String name;

        public CustomObj(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "I'm " + name;
        }
    }

    static class CustomConstraint {
        @Override
        public String toString() {
            return "I'm custom constraint";
        }
    }

    static class Entity {
        Entity(String name) {
        }
    }
}
