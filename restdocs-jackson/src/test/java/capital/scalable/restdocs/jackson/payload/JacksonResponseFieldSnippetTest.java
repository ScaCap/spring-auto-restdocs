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

import static org.springframework.util.ClassUtils.getMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.method.HandlerMethod;

public class JacksonResponseFieldSnippetTest extends AbstractSnippetTests {

    private ObjectMapper mapper = new ObjectMapper();

    public JacksonResponseFieldSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void simpleResponse() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(),
                getMethod(TestResource.class, "getItem"));

        this.snippet.expectResponseFields("map-response").withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "")
                        .row("field2", "Number", "true", ""));

        new JacksonResponseFieldSnippet().document(operationBuilder("map-response")
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .request("http://localhost")
                .build());
    }

    private static class TestResource {

        public Item getItem() {
            return new Item("test");
        }
    }

    private static class Item {
        @NotBlank
        private String field1;
        private Integer field2;

        public Item(String field1) {
            this.field1 = field1;
        }

        public String getField1() {
            return field1;
        }

        public Integer getField2() {
            return field2;
        }
    }
}
