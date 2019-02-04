/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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
package capital.scalable.restdocs.payload;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_REQUEST_FIELDS;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class JacksonRequestFieldSnippetTest extends AbstractSnippetTests {
    private ObjectMapper mapper;
    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public JacksonRequestFieldSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        javadocReader = mock(JavadocReader.class);
        constraintReader = mock(ConstraintReader.class);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem", Item.class);
        mockFieldComment(Item.class, "field1", "A string");
        mockFieldComment(Item.class, "field2", "An integer");
        mockOptionalMessage(Item.class, "field1", "false");
        mockConstraintMessage(Item.class, "field2", "A constraint");

        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "A string.")
                        .row("field2", "Integer", "true", "An integer.\n\nA constraint."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithEnum() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItemWithWeight",
                ItemWithWeight.class);
        mockFieldComment(ItemWithWeight.class, "weight", "An enum");
        mockConstraintMessage(ItemWithWeight.class, "weight", "Must be one of [LIGHT, HEAVY]");

        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("weight", "String", "true",
                                "An enum.\n\nMust be one of [LIGHT, HEAVY]."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void noRequestBody() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "addItem2");

        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(equalTo("No request body."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .build());
    }

    @Test
    public void noHandlerMethod() throws Exception {
        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(equalTo("No request body."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(ObjectMapper.class.getName(), mapper)
                .build());
    }

    @Test
    public void listRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItems", List.class);
        mockFieldComment(Item.class, "field1", "A string");
        mockFieldComment(Item.class, "field2", "An integer");

        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("[].field1", "String", "true", "A string.")
                        .row("[].field2", "Integer", "true", "An integer."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), mock(ConstraintReader.class))
                .build());
    }

    @Test
    public void jsonSubTypesRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addSubItem", ParentItem.class);
        mockFieldComment(ParentItem.class, "type", "A type");
        mockFieldComment(ParentItem.class, "commonField", "A common field");
        mockFieldComment(SubItem1.class, "subItem1Field", "A sub item 1 field");
        mockFieldComment(SubItem2.class, "subItem2Field", "A sub item 2 field");

        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(
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
                .build());
    }

    @Test
    public void exactRequestType() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("processItem", String.class);
        mockFieldComment(ProcessingCommand.class, "command", "A command");

        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("command", "String", "true", "A command."));

        new JacksonRequestFieldSnippet().requestBodyAsType(ProcessingCommand.class)
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(ObjectMapper.class.getName(), mapper)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ConstraintReader.class.getName(), constraintReader)
                        .build());
    }

    @Test
    public void hasContentWithRequestBodyAnnotation() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem", Item.class);

        boolean hasContent = new JacksonRequestFieldSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent, is(true));
    }

    @Test
    public void hasContentWithModelAttributeAnnotation() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("processItem", String.class);

        boolean hasContent = new JacksonRequestFieldSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent, is(true));
    }

    @Test
    public void noContent() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem2");

        boolean hasContent = new JacksonRequestFieldSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent, is(false));
    }

    @Test
    public void failOnUndocumentedFields() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem", Item.class);

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following request fields were not documented: [field1, field2]");

        new JacksonRequestFieldSnippet().failOnUndocumentedFields(true).document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void deprecated() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("removeItem", DeprecatedItem.class);
        mockFieldComment(DeprecatedItem.class, "index", "item's index");
        mockDeprecated(DeprecatedItem.class, "index", "use index2");

        this.snippets.expect(AUTO_REQUEST_FIELDS).withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("index", "Integer", "true",
                                "**Deprecated.** Use index2.\n\nItem's index."));

        new JacksonRequestFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    private void mockConstraintMessage(Class<?> type, String fieldName, String comment) {
        when(constraintReader.getConstraintMessages(type, fieldName))
                .thenReturn(singletonList(comment));
    }

    private void mockOptionalMessage(Class<?> type, String fieldName, String comment) {
        when(constraintReader.getOptionalMessages(type, fieldName))
                .thenReturn(singletonList(comment));
    }

    private void mockFieldComment(Class<?> type, String fieldName, String comment) {
        when(javadocReader.resolveFieldComment(type, fieldName))
                .thenReturn(comment);
    }

    private void mockDeprecated(Class<?> type, String fieldName, String comment) {
        when(javadocReader.resolveFieldTag(type, fieldName, "deprecated"))
                .thenReturn(comment);
    }

    private HandlerMethod createHandlerMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return new HandlerMethod(new TestResource(), name, parameterTypes);
    }

    private static class TestResource {

        public void addItem(@RequestBody Item item) {
            // NOOP
        }

        public void addItemWithWeight(@RequestBody ItemWithWeight item) {
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

        public void processItem(@ModelAttribute String item) {
            // NOOP
        }

        public void removeItem(@RequestBody DeprecatedItem item) {
            // NOOP
        }
    }

    private static class ProcessingCommand {
        private String command;
    }

    private static class Item {
        @NotBlank
        private String field1;
        @Size(max = 10)
        private Integer field2;
    }

    private enum Weight {
        LIGHT, HEAVY
    }

    private static class ItemWithWeight {
        private Weight weight;
    }

    private static class DeprecatedItem {
        @Deprecated
        private int index;
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
