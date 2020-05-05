/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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

import static capital.scalable.restdocs.SnippetRegistry.AUTO_MODELATTRIBUTE;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.jackson.SardObjectMapper;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.HandlerMethod;

public class JacksonModelAttributeSnippetTest extends AbstractSnippetTests {
    private ObjectMapper mapper;
    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public JacksonModelAttributeSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Before
    public void setup() {
        mapper = new SardObjectMapper(new ObjectMapper());
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

        new JacksonModelAttributeSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "A string.")
                        .row("field2", "Integer", "true", "An integer.\n\nA constraint."));
    }

    @Test
    public void simpleRequestWithEnum() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItemWithWeight",
                ItemWithWeight.class);
        mockFieldComment(ItemWithWeight.class, "weight", "An enum");
        mockConstraintMessage(ItemWithWeight.class, "weight", "Must be one of [LIGHT, HEAVY]");

        new JacksonModelAttributeSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("weight", "String", "true",
                                "An enum.\n\nMust be one of [LIGHT, HEAVY]."));
    }

    @Test
    public void noParameters() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "addItem2");

        new JacksonModelAttributeSnippet()
                .document(operationBuilder.attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(ObjectMapper.class.getName(), mapper).build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).isEqualTo("No parameters.");
    }

    @Test
    public void noHandlerMethod() throws Exception {
        new JacksonModelAttributeSnippet().document(operationBuilder
                .attribute(ObjectMapper.class.getName(), mapper)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).isEqualTo("No parameters.");
    }

    @Test
    public void listRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItems", List.class);
        mockFieldComment(Item.class, "field1", "A string");
        mockFieldComment(Item.class, "field2", "An integer");

        new JacksonModelAttributeSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), mock(ConstraintReader.class))
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("[].field1", "String", "true", "A string.")
                        .row("[].field2", "Integer", "true", "An integer."));
    }

    @Test
    public void jsonSubTypesRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addSubItem", ParentItem.class);
        mockFieldComment(ParentItem.class, "type", "A type");
        mockFieldComment(ParentItem.class, "commonField", "A common field");
        mockFieldComment(SubItem1.class, "subItem1Field", "A sub item 1 field");
        mockFieldComment(SubItem2.class, "subItem2Field", "A sub item 2 field");

        new JacksonModelAttributeSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("type", "String", "true", "A type.")
                        .row("commonField", "String", "true", "A common field.")
                        .row("subItem1Field", "Boolean", "true", "A sub item 1 field.")
                        .row("subItem2Field", "Integer", "true", "A sub item 2 field."));
    }

    @Test
    public void hasContentWithModelAttributeAnnotation() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem", Item.class);

        boolean hasContent = new JacksonModelAttributeSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent).isTrue();
    }

    @Test
    public void noContent() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem2");

        boolean hasContent = new JacksonModelAttributeSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent).isFalse();
    }

    @Test
    public void failOnUndocumentedParameters() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem", Item.class);

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following query parameters were not documented: [field1, field2]");

        new JacksonModelAttributeSnippet(null, true).document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod).attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader).build());
    }

    @Test
    public void failOnUndocumentedFields() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItemPost", Item.class);

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following request fields were not documented: [field1, field2]");

        new JacksonModelAttributeSnippet(null, true).document(operationBuilder
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

        new JacksonModelAttributeSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("index", "Integer", "true",
                                "**Deprecated.** Use index2.\n\nItem's index."));
    }

    @Test
    public void accessors() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("accessors", ReadWriteAccessors.class);

        new JacksonModelAttributeSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_MODELATTRIBUTE)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("writeOnly", "String", "true", "")
                        .row("bothWays", "String", "true", ""));
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

        public void addItem(@ModelAttribute Item item) {
            // NOOP
        }

        @PostMapping
        public void addItemPost(@ModelAttribute Item item) {
            // NOOP
        }

        public void addItemWithWeight(@ModelAttribute ItemWithWeight item) {
            // NOOP
        }

        public void addItems(@ModelAttribute List<Item> items) {
            // NOOP
        }

        public void addItem2() {
            // NOOP
        }

        public void addSubItem(@ModelAttribute ParentItem item) {
            // NOOP
        }

        public void removeItem(@ModelAttribute DeprecatedItem item) {
            // NOOP
        }

        public void accessors(@ModelAttribute ReadWriteAccessors accessors) {
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

    private static class ReadWriteAccessors {
        @JsonProperty(access = READ_ONLY)
        private String readOnly;
        @JsonProperty(access = WRITE_ONLY)
        private String writeOnly;
        private String bothWays;
    }
}
