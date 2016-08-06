package capital.scalable.restdocs.jackson.constraints;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
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
        configuration.put("groups", new Class<?>[]{ExampleConstraintGroup.class});
        configuration.put("payload", new Class<?>[0]);
        Constraint constraint = new Constraint("Custom", configuration);

        when(delegate.resolveForProperty("prop", this.getClass()))
                .thenReturn(singletonList(constraint));

        resolver = new HumanReadableConstraintResolver(delegate);

        // when
        List<Constraint> constraints = resolver.resolveForProperty("prop", this.getClass());

        // then
        assertThat(constraints.size(), is(1));
        assertThat(constraints.get(0).getConfiguration().size(), is(7));
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