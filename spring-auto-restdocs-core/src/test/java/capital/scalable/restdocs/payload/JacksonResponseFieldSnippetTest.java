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

import static capital.scalable.restdocs.payload.TableWithPrefixMatcher.tableWithPrefix;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.web.method.HandlerMethod;

public class JacksonResponseFieldSnippetTest extends AbstractSnippetTests {

    private ObjectMapper mapper;
    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public JacksonResponseFieldSnippetTest(String name, TemplateFormat templateFormat) {
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
    public void simpleResponse() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("getItem");
        mockFieldComment(Item.class, "field1", "A string");
        mockFieldComment(Item.class, "field2", "A decimal");
        mockMandatoryConstraint(NotBlank.class, true);
        mockOptionalMessage(Item.class, "field1", "false");
        mockConstraintMessage(Item.class, "field2", "A constraint");

        this.snippets.expectResponseFields().withContents(tableWithPrefix("\n",
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "A string.")
                        .row("field2", "Decimal", "true",
                                "A decimal." + lineBreak() + "A constraint.")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void listResponse() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("getItems");
        mockFieldComment(Item.class, "field1", "A string");
        mockFieldComment(Item.class, "field2", "A decimal");

        this.snippets.expectResponseFields().withContents(tableWithPrefix("\n",
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("[].field1", "String", "true", "A string.")
                        .row("[].field2", "Decimal", "true", "A decimal.")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void noResponseBody() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("noItem");

        this.snippets.expectResponseFields().withContents(equalTo("No response body."));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .build());
    }

    @Test
    public void pageResponse() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("pagedItems");
        mockFieldComment(Item.class, "field1", "A string");
        mockFieldComment(Item.class, "field2", "A decimal");

        mockMandatoryConstraint(NotBlank.class, true);
        mockOptionalMessage(Item.class, "field1", "false");
        mockConstraintMessage(Item.class, "field2", "A constraint");

        this.snippets.expectResponseFields().withContents(
                tableWithPrefix(paginationPrefix(),
                        tableWithHeader("Path", "Type", "Optional", "Description")
                                .row("field1", "String", "false", "A string.")
                                .row("field2", "Decimal", "true",
                                        "A decimal." + lineBreak() + "A constraint.")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void responseEntityResponse() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("responseEntityItem");
        mockFieldComment(Item.class, "field1", "A string");
        mockFieldComment(Item.class, "field2", "A decimal");
        mockMandatoryConstraint(NotBlank.class, true);
        mockOptionalMessage(Item.class, "field1", "false");
        mockConstraintMessage(Item.class, "field2", "A constraint");

        this.snippets.expectResponseFields().withContents(tableWithPrefix("\n",
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "A string.")
                        .row("field2", "Decimal", "true",
                                "A decimal." + lineBreak() + "A constraint.")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void exactResponseType() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("processItem");
        mockFieldComment(ProcessingResponse.class, "output", "An output");

        this.snippets.expectResponseFields().withContents(tableWithPrefix("\n",
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("output", "String", "true", "An output.")));

        new JacksonResponseFieldSnippet().responseBodyAsType(ProcessingResponse.class)
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(ObjectMapper.class.getName(), mapper)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ConstraintReader.class.getName(), constraintReader)
                        .build());
    }

    @Test
    public void hasContent() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("getItem");

        boolean hasContent = new JacksonResponseFieldSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent, is(true));
    }

    @Test
    public void noContent() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("noItem");

        boolean hasContent = new JacksonResponseFieldSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent, is(false));
    }

    @Test
    public void failOnUndocumentedFields() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("getItem");

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following response fields were not documented: [field1, field2]");

        new JacksonResponseFieldSnippet().failOnUndocumentedFields(true).document(operationBuilder
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

    private void mockMandatoryConstraint(Class<?> annotation, boolean mandatory) {
        when(constraintReader.isMandatory(annotation)).thenReturn(mandatory);
    }

    private void mockFieldComment(Class<?> type, String fieldName, String comment) {
        when(javadocReader.resolveFieldComment(type, fieldName))
                .thenReturn(comment);
    }

    private String paginationPrefix() {
        if ("adoc".equals(templateFormat.getFileExtension())) {
            return "Standard <<overview-pagination,paging>> response where `content` field is"
                    + " list of following objects:\n";
        } else {
            return "Standard [paging](#overview-pagination) response where `content` field is"
                    + " list of following objects:\n";
        }
    }

    private String lineBreak() {
        return templateFormat.getId().equals(TemplateFormats.asciidoctor().getId())
                ? " +\n" : "<br>";
    }

    private HandlerMethod createHandlerMethod(String responseEntityItem)
            throws NoSuchMethodException {
        return new HandlerMethod(new TestResource(), responseEntityItem);
    }

    private static class TestResource {

        public Item getItem() {
            return new Item("test");
        }

        public List<Item> getItems() {
            return Collections.singletonList(new Item("test"));
        }

        public void noItem() {
            // NOOP
        }

        public Page<Item> pagedItems() {
            return new PageImpl<>(singletonList(new Item("test")));
        }

        public ResponseEntity<Item> responseEntityItem() {
            return ResponseEntity.ok(new Item("test"));
        }

        public String processItem() {
            return "";
        }
    }

    private static class Item {
        @NotBlank
        private String field1;
        @DecimalMin("1")
        @DecimalMax("10")
        private BigDecimal field2;

        public Item(String field1) {
            this.field1 = field1;
        }
    }

    private static class ProcessingResponse {
        private String output;
    }
}
