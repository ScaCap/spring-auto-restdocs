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

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

public class HumanReadableConstraintResolverTest {
    private HumanReadableConstraintResolver resolver;

    private ConstraintResolver delegate;

    @Before
    public void setup() {
        delegate = mock(ConstraintResolver.class);
    }

    @Test
    public void testHumanReadable() {
        // setup
        Map<String, Object> configuration = new HashedMap();
        configuration.put("primitive", 1);
        configuration.put("wrapper", Integer.valueOf(1));
        configuration.put("object", new CustomObj("Peter"));
        configuration.put("array", new Object[]{"value1", "value2"});
        configuration.put("class", CustomConstraint.class);
        Constraint constraint = new Constraint("Custom", configuration);

        when(delegate.resolveForProperty("prop", this.getClass()))
                .thenReturn(singletonList(constraint));

        resolver = new HumanReadableConstraintResolver(delegate);

        // when
        List<Constraint> constraints = resolver.resolveForProperty("prop", this.getClass());

        // then
        assertThat(constraints.size(), is(1));
        assertThat(constraints.get(0).getConfiguration().size(), is(5));
        assertThat(constraints.get(0).getConfiguration().get("primitive").toString(), is("1"));
        assertThat(constraints.get(0).getConfiguration().get("wrapper").toString(), is("1"));
        assertThat(constraints.get(0).getConfiguration().get("object").toString(), is("I'm Peter"));
        assertThat(constraints.get(0).getConfiguration().get("array").toString(),
                is("[value1, value2]"));
        assertThat(constraints.get(0).getConfiguration().get("class").toString(),
                is("I'm custom constraint"));
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
}