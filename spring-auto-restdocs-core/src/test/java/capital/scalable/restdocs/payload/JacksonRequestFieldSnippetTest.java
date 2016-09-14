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

package capital.scalable.restdocs.payload;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.Size;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class JacksonRequestFieldSnippetTest extends AbstractSnippetTests {

    public JacksonRequestFieldSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void simpleRequest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "addItem", Item.class);
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(Item.class, "field1"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(Item.class, "field2"))
                .thenReturn("An integer");

        ConstraintReader constraintReader = mock(ConstraintReader.class);
        when(constraintReader.isMandatory(NotBlank.class)).thenReturn(true);
        when(constraintReader.getOptionalMessages(Item.class, "field1"))
                .thenReturn(singletonList("false"));
        when(constraintReader.getConstraintMessages(Item.class, "field2"))
                .thenReturn(singletonList("A constraint"));

        this.snippet.expectRequestFields().withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "A string")
                        .row("field2", "Integer", "true", "An integer +\nA constraint"));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .request("http://localhost")
                .content("{\"field1\":\"test\"}")
                .build());
    }

    @Test
    public void noRequestBody() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "addItem2");

        this.snippet.expectRequestFields().withContents(
                equalTo("No request body."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .request("http://localhost")
                .build());
    }

    private static class TestResource {

        public void addItem(@RequestBody Item item) {
            // NOOP
        }

        public void addItem2() {
            // NOOP
        }
    }

    private static class Item {
        @NotBlank
        private String field1;
        @Size(max = 10)
        private Integer field2;
    }
}
