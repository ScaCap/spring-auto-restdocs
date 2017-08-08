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

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.springframework.core.MethodParameter;

public class ConstraintReaderImplTest {

    private ConstraintReader reader = new ConstraintReaderImpl();

    @Test
    public void getConstraintMessages() {
        List<String> messages = reader.getConstraintMessages(Constraintz.class, "name");
        assertThat(messages.size(), is(0));

        messages = reader.getConstraintMessages(Constraintz.class, "index");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be at least 1"));

        messages = reader.getConstraintMessages(Constraintz.class, "items");
        assertThat(messages.size(), is(0));

        messages = reader.getConstraintMessages(Constraintz.class, "amount");
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0), is("Must be at least 10"));
        assertThat(messages.get(1), is("Must be at most 1000"));

        messages = reader.getConstraintMessages(Constraintz.class, "type");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [big, small]"));

        messages = reader.getConstraintMessages(Constraintz.class, "amountWithGroup");
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0), is("Must be at least 10 (update)"));
        assertThat(messages.get(1),
                is("Must be at most 1000 (update), Must be at most 1000 (create)"));

        messages = reader.getConstraintMessages(Constraintz.class, "indexWithGroup");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be null (update)"));

        messages = reader.getConstraintMessages(Constraintz.class, "num");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be at most 10 (groups: [UnresolvedGroup])"));

        messages = reader.getConstraintMessages(Constraintz.class, "bool");
        assertThat(messages.size(), is(0));

        messages = reader.getConstraintMessages(Constraintz.class, "str");
        assertThat(messages.size(), is(0)); // array

        messages = reader.getConstraintMessages(Constraintz.class, "enum1");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [ONE, TWO]"));

        messages = reader.getConstraintMessages(Constraintz.class, "enum2");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Custom enum description: [A, B]"));
    }

    @Test
    public void getOptionalMessages() {
        List<String> messages = reader.getOptionalMessages(Constraintz.class, "name");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "index");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "items");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "amount");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "type");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "amountWithGroup");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "indexWithGroup");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false (create)"));

        messages = reader.getOptionalMessages(Constraintz.class, "num");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "bool");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "str");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "enum1");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "enum2");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));
    }

    @Test
    public void getParameterConstraintMessages() throws NoSuchMethodException {
        Method method = MethodTest.class.getMethod("exec", Integer.class, String.class);

        List<String> messages = reader.getConstraintMessages(new MethodParameter(method, 0));
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0), is("Must be at least 1 (create)"));
        assertThat(messages.get(1), is("Must be at most 2 (update)"));

        messages = reader.getConstraintMessages(new MethodParameter(method, 1));
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [all, single]"));
    }

    static class Constraintz {
        @NotBlank
        private String name;

        @NotNull
        @Min(1)
        private Integer index;

        @Valid
        @NotEmpty
        private Collection<Boolean> items;

        @DecimalMin("10")
        @DecimalMax("1000")
        private BigDecimal amount;

        @OneOf({"big", "small"})
        private String type;

        @DecimalMin(value = "10", groups = Update.class)
        @DecimalMax(value = "1000", groups = {Update.class, Create.class})
        private BigDecimal amountWithGroup;

        @Null(groups = Update.class)
        @NotNull(groups = Create.class)
        private Integer indexWithGroup;

        @Max(value = 10, groups = UnresolvedGroup.class)
        private long num;

        private boolean bool;

        private char[] str;

        private Enum1 enum1;

        @NotNull
        private Enum2 enum2;
    }

    enum Enum1 {ONE, TWO}

    enum Enum2 {A, B}

    interface UnresolvedGroup {
    }

    static class MethodTest {
        public void exec(
                @NotNull
                @Min(value = 1, groups = Create.class)
                @Max(value = 2, groups = Update.class) Integer count,
                @NotBlank
                @OneOf({"all", "single"}) String type) {
        }
    }
}
