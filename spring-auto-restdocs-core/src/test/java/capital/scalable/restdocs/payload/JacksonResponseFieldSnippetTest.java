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
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.web.method.HandlerMethod;

public class JacksonResponseFieldSnippetTest extends AbstractSnippetTests {

    public JacksonResponseFieldSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void simpleResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItem");
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(Item.class, "field1"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(Item.class, "field2"))
                .thenReturn("A decimal");

        ConstraintReader constraintReader = mock(ConstraintReader.class);
        when(constraintReader.isMandatory(NotBlank.class)).thenReturn(true);
        when(constraintReader.getOptionalMessages(Item.class, "field1"))
                .thenReturn(singletonList("false"));
        when(constraintReader.getConstraintMessages(Item.class, "field2"))
                .thenReturn(singletonList("A constraint"));

        this.snippet.expectResponseFields().withContents(
                tableWithPrefix("\n",
                        tableWithHeader("Path", "Type", "Optional", "Description")
                                .row("field1", "String", "false", "A string")
                                .row("field2", "Decimal", "true",
                                        "A decimal" + lineBreak() + "A constraint")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .request("http://localhost")
                .build());
    }

    @Test
    public void listResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItems");
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(Item.class, "field1"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(Item.class, "field2"))
                .thenReturn("A decimal");

        this.snippet.expectResponseFields().withContents(
                tableWithPrefix("\n",
                        tableWithHeader("Path", "Type", "Optional", "Description")
                                .row("[].field1", "String", "true", "A string")
                                .row("[].field2", "Decimal", "true", "A decimal")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), mock(ConstraintReader.class))
                .request("http://localhost")
                .build());
    }

    @Test
    public void noResponseBody() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "noItem");

        this.snippet.expectResponseFields().withContents(
                equalTo("No response body."));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .request("http://localhost")
                .build());
    }

    @Test
    public void pageResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "pagedItems");
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(Item.class, "field1"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(Item.class, "field2"))
                .thenReturn("A decimal");

        ConstraintReader constraintReader = mock(ConstraintReader.class);
        when(constraintReader.isMandatory(NotBlank.class)).thenReturn(true);
        when(constraintReader.getOptionalMessages(Item.class, "field1"))
                .thenReturn(singletonList("false"));
        when(constraintReader.getConstraintMessages(Item.class, "field2"))
                .thenReturn(singletonList("A constraint"));

        this.snippet.expectResponseFields().withContents(
                tableWithPrefix(paginationPrefix(),
                        tableWithHeader("Path", "Type", "Optional", "Description")
                                .row("field1", "String", "false", "A string")
                                .row("field2", "Decimal", "true",
                                        "A decimal" + lineBreak() + "A constraint")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .request("http://localhost")
                .build());
    }

    @Test
    public void responseEntityResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "responseEntityItem");
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(Item.class, "field1"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(Item.class, "field2"))
                .thenReturn("A decimal");

        ConstraintReader constraintReader = mock(ConstraintReader.class);
        when(constraintReader.isMandatory(NotBlank.class)).thenReturn(true);
        when(constraintReader.getOptionalMessages(Item.class, "field1"))
                .thenReturn(singletonList("false"));
        when(constraintReader.getConstraintMessages(Item.class, "field2"))
                .thenReturn(singletonList("A constraint"));

        this.snippet.expectResponseFields().withContents(
                tableWithPrefix("\n",
                        tableWithHeader("Path", "Type", "Optional", "Description")
                                .row("field1", "String", "false", "A string")
                                .row("field2", "Decimal", "true",
                                        "A decimal" + lineBreak() + "A constraint")));

        new JacksonResponseFieldSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .request("http://localhost")
                .build());
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
}
