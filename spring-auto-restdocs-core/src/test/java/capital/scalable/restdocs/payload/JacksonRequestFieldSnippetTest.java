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

import static capital.scalable.restdocs.OperationAttributeHelper.determineLineBreak;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.Size;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
                .thenReturn("An integer<br>\n Very important\n <p>\n field");

        ConstraintReader constraintReader = mock(ConstraintReader.class);
        when(constraintReader.isMandatory(NotBlank.class)).thenReturn(true);
        when(constraintReader.getOptionalMessages(Item.class, "field1"))
                .thenReturn(singletonList("false"));
        when(constraintReader.getConstraintMessages(Item.class, "field2"))
                .thenReturn(singletonList("A constraint"));

        this.snippets.expectRequestFields().withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "A string.")
                        .row("field2", "Integer", "true", "An integer" + lineBreak()
                                + "Very important" + lineBreak() + lineBreak()
                                + "field." + lineBreak()
                                + "A constraint."));

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

        this.snippets.expectRequestFields().withContents(
                equalTo("No request body."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .request("http://localhost")
                .build());
    }

    @Test
    public void listRequest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "addItems", List.class);
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(Item.class, "field1"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(Item.class, "field2"))
                .thenReturn("An integer");

        this.snippets.expectRequestFields().withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("[].field1", "String", "true", "A string.")
                        .row("[].field2", "Integer", "true", "An integer."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), mock(ConstraintReader.class))
                .request("http://localhost")
                .content("{\"field1\":\"test\"}")
                .build());
    }

    @Test
    public void jsonSubTypesRequest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "addSubItem",
                ParentItem.class);
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(ParentItem.class, "type"))
                .thenReturn("A type");
        when(javadocReader.resolveFieldComment(ParentItem.class, "commonField"))
                .thenReturn("A common field");
        when(javadocReader.resolveFieldComment(SubItem1.class, "subItem1Field"))
                .thenReturn("A sub item 1 field");
        when(javadocReader.resolveFieldComment(SubItem2.class, "subItem2Field"))
                .thenReturn("A sub item 2 field");

        ConstraintReader constraintReader = mock(ConstraintReader.class);

        this.snippets.expectRequestFields().withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("type", "String", "true", "A type.")
                        .row("commonField", "String", "true", "A common field.")
                        .row("subItem1Field", "Boolean", "true", "A sub item 1 field.")
                        .row("subItem2Field", "Integer", "true", "A sub item 2 field."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .request("http://localhost")
                .content("{\"type\":\"1\"}")
                .build());
    }

    private String lineBreak() {
        return determineLineBreak(templateFormat);
    }

    private static class TestResource {

        public void addItem(@RequestBody Item item) {
            // NOOP
        }

        public void addItems(@RequestBody List<Item> items) {
            // NOOP
        }

        public void addItem2() {
            // NOOP
        }

        public void addSubItem(@RequestBody ParentItem item) {
            // NOOP
        }
    }

    private static class Item {
        @NotBlank
        private String field1;
        @Size(max = 10)
        private Integer field2;
    }


    @JsonTypeInfo(use = NAME, include = PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SubItem1.class, name = "1"),
            @JsonSubTypes.Type(value = SubItem2.class, name = "2")
    })
    private static abstract class ParentItem {
        private String type;
        private String commonField;

    }

    private static class SubItem1 extends ParentItem {
        private Boolean subItem1Field;
    }

    private static class SubItem2 extends ParentItem {
        private Integer subItem2Field;
    }
}
