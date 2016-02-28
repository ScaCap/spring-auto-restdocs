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

package capital.scalable.restdocs.jackson.payload;

import static capital.scalable.restdocs.jackson.test.SnippetMatchers.tableWithHeader;

import java.lang.reflect.Method;

import capital.scalable.restdocs.jackson.test.ExpectedSnippet;
import capital.scalable.restdocs.jackson.test.FakeMvcResult;
import capital.scalable.restdocs.jackson.test.OperationBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.RequestBody;

public class JacksonRequestFieldSnippetTest {

    @Rule
    public final ExpectedSnippet snippet = new ExpectedSnippet();

    @Test
    public void simpleRequest() throws Exception {
        TestResource bean = new TestResource();
        Method method = method(TestResource.class, "addItem");
        MvcResult mvcResult = FakeMvcResult.build(bean, method);
        ObjectMapper mapper = new ObjectMapper();

        this.snippet.expectRequestFields("map-request").withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "")
                        .row("field2", "Number", "true", ""));

        new JacksonRequestFieldSnippet().document(new OperationBuilder(
                "map-request", this.snippet.getOutputDirectory())
                .attribute(MvcResult.class.getName(), mvcResult)
                .attribute(ObjectMapper.class.getName(), mapper)
                .request("http://localhost")
                .content("{\"field1\":\"test\"}")
                .build());
    }

    private Method method(Class<?> clazz, String name) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Unknown method " + name);
    }

    private class TestResource {

        public void addItem(@RequestBody Item item) {
            // NOOP
        }
    }

    private class Item {
        @NotBlank
        private String field1;
        private Integer field2;

        public String getField1() {
            return field1;
        }

        public Integer getField2() {
            return field2;
        }
    }
}
