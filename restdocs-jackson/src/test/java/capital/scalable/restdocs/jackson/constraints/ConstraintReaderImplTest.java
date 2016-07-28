package capital.scalable.restdocs.jackson.constraints;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
    public void testMandatoryAnnotations() {
        assertThat(reader.isMandatory(NotNull.class), is(true));
        assertThat(reader.isMandatory(NotBlank.class), is(true));
        assertThat(reader.isMandatory(NotEmpty.class), is(true));

        // sanity check
        assertThat(reader.isMandatory(Size.class), is(false));
        assertThat(reader.isMandatory(Override.class), is(false));
        assertThat(reader.isMandatory(OneOf.class), is(false));
    }

    @Test
    public void testConstraintMessages() {
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

        messages = reader.getConstraintMessages(Constraintz.class, "items");
        assertThat(messages.size(), is(0));

        messages = reader.getConstraintMessages(Constraintz.class, "type");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [big, small]"));
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

        private long num;
    }
}
