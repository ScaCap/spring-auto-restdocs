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

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

public class ConstraintReaderImplTest {

    private ConstraintReader reader = new ConstraintReaderImpl();

    @Test
    public void isMandatory() {
        assertThat(reader.isMandatory(NotNull.class), is(true));
        assertThat(reader.isMandatory(NotBlank.class), is(true));
        assertThat(reader.isMandatory(NotEmpty.class), is(true));

        // sanity check
        assertThat(reader.isMandatory(Size.class), is(false));
        assertThat(reader.isMandatory(Override.class), is(false));
        assertThat(reader.isMandatory(OneOf.class), is(false));
    }

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

        messages = reader.getOptionalMessages(Constraintz.class, "items");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "type");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "amountWithGroup");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "indexWithGroup");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false (create)"));
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

        private long num;
    }
}
