package capital.scalable.restdocs.jackson.response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class ArrayLimitingJsonContentModifierTest {

    @Test
    public void testModifyJsonWithMoreThanThreeItems() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        ArrayLimitingJsonContentModifier modifier = new ArrayLimitingJsonContentModifier(mapper);
        WithArray object = WithArray.withItems(100);
        JsonNode root = mapper.readTree(mapper.writeValueAsString(object));
        // when
        modifier.modifyJson(root);
        // then
        assertThat(root.get("items").size(), is(3));
        assertThat(root.get("ignore").isNumber(), is(true));
    }

    @Test
    public void testModifyJsonWithLessThanThreeItems() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        ArrayLimitingJsonContentModifier modifier = new ArrayLimitingJsonContentModifier(mapper);
        WithArray object = WithArray.withItems(2);
        JsonNode root = mapper.readTree(mapper.writeValueAsString(object));
        // when
        modifier.modifyJson(root);
        // then
        assertThat(root.get("items").size(), is(2));
        assertThat(root.get("ignore").isNumber(), is(true));
    }

    private static class WithArray {
        private final Integer ignore;
        private final List<String> items;

        public WithArray(List<String> items) {
            this.ignore = 42;
            this.items = items;
        }

        public static WithArray withItems(int count) {
            List<String> items = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                items.add(Integer.toString(i));
            }
            return new WithArray(items);
        }

        public Integer getIgnore() {
            return ignore;
        }

        public List<String> getItems() {
            return items;
        }
    }
}
