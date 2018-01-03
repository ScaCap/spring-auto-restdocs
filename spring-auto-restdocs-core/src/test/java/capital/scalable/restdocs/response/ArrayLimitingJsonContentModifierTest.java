/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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
package capital.scalable.restdocs.response;

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
