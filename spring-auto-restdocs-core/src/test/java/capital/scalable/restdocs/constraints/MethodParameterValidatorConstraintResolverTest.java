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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;

public class MethodParameterValidatorConstraintResolverTest {

    @Test
    public void resolveConstraints() throws NoSuchMethodException {
        MethodParameterConstraintResolver resolver = new
                MethodParameterValidatorConstraintResolver();

        Method method = ConstraintTest.class.getMethod("method", Long.class, String.class);

        List<Constraint> constraints = resolver.resolveForParameter(new MethodParameter(method, 0));
        assertThat(constraints.size(), is(2));

        Constraint constraint = constraints.get(0);
        assertThat(constraint.getName(), is(NotNull.class.getName()));
        Map<String, Object> config = constraint.getConfiguration();
        assertThat(config.size(), is(3)); // default ones

        constraint = constraints.get(1);
        assertThat(constraint.getName(), is(Min.class.getName()));
        config = constraint.getConfiguration();
        assertThat(config.size(), is(4)); // +value
        assertThat(config.get("value"), is((Object) 1L));
        assertThat(config.get("groups"), is((Object) new Object[]{Create.class}));

        constraints = resolver.resolveForParameter(new MethodParameter(method, 1));
        assertThat(constraints.size(), is(2));

        constraint = constraints.get(0);
        assertThat(constraint.getName(), is(NotBlank.class.getName()));
        config = constraint.getConfiguration();
        assertThat(config.size(), is(3)); // default ones

        constraint = constraints.get(1);
        assertThat(constraint.getName(), is(OneOf.class.getName()));
        config = constraint.getConfiguration();
        assertThat(config.size(), is(4)); // +value
        assertThat(config.get("value"), is((Object) new Object[]{"one", "two", "three"}));
    }

    static class ConstraintTest {
        public void method(@NotNull @Min(value = 1, groups = Create.class) Long index,
                @NotBlank @OneOf({"one", "two", "three"}) String picker) {
        }
    }
}