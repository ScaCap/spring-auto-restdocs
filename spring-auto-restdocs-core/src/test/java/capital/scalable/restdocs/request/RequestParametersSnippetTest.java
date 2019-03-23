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
package capital.scalable.restdocs.request;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_REQUEST_PARAMETERS;
import static capital.scalable.restdocs.payload.TableWithPrefixMatcher.tableWithPrefix;
import static capital.scalable.restdocs.util.FormatUtil.fixLineSeparator;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Optional;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

public class RequestParametersSnippetTest extends AbstractSnippetTests {
    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public RequestParametersSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Before
    public void setup() {
        javadocReader = mock(JavadocReader.class);
        constraintReader = mock(ConstraintReader.class);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem", Integer.class,
                String.class);
        initParameters(handlerMethod);
        mockParamComment("searchItem", "type", "An integer");
        mockParamComment("searchItem", "description", "A string");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("type", "Integer", "false", "An integer.")
                        .row("text", "String", "true", "A string."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithPrimitives() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem2", double.class,
                boolean.class, int.class);
        initParameters(handlerMethod);
        mockParamComment("searchItem2", "param1", "A decimal");
        mockParamComment("searchItem2", "param2", "A boolean");
        mockParamComment("searchItem2", "param3", "An integer");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("param1", "Decimal", "false", "A decimal.")
                        .row("param2", "Boolean", "false", "A boolean.")
                        .row("param3", "Integer", "true", "An integer.\n\nDefault value: '1'."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithPrimitivesDefaultValueParameterNotDocumented() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem2", double.class,
                boolean.class, int.class);
        initParameters(handlerMethod);
        mockParamComment("searchItem2", "param1", "A decimal");
        mockParamComment("searchItem2", "param2", "A boolean");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("param1", "Decimal", "false", "A decimal.")
                        .row("param2", "Boolean", "false", "A boolean.")
                        .row("param3", "Integer", "true", "Default value: '1'."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithStringDefaultValueParameter() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem2String", double.class,
                boolean.class, String.class);
        initParameters(handlerMethod);
        mockParamComment("searchItem2String", "param1", "A decimal");
        mockParamComment("searchItem2String", "param2", "A boolean");
        mockParamComment("searchItem2String", "param3", "A String");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("param1", "Decimal", "false", "A decimal.")
                        .row("param2", "Boolean", "false", "A boolean.")
                        .row("param3", "String", "true", "A String.\n\nDefault value: 'de'."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithCustomTypes() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem5", Locale.class);
        initParameters(handlerMethod);
        mockParamComment("searchItem5", "locale", "A locale");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("locale", "String", "false", "A locale."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithEnum() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItemBySize",
                TestResource.Size.class);
        initParameters(handlerMethod);
        mockParamComment("searchItemBySize", "size", "An enum");
        mockConstraintMessage(handlerMethod.getMethodParameters()[0],
                "Must be one of [SMALL, LARGE]");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("size", "String", "false",
                                "An enum.\n\nMust be one of [SMALL, LARGE]."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithOptional() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItemOptional", Optional.class);
        initParameters(handlerMethod);
        mockParamComment("searchItemOptional", "param1", "An optional string");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("param1", "String", "true", "An optional string."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void noParameters() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("items");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).isEqualTo("No parameters.");

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
    }

    @Test
    public void noHandlerMethod() throws Exception {
        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).isEqualTo("No parameters.");

        new RequestParametersSnippet().document(operationBuilder
                .build());
    }

    @Test
    public void failOnUndocumentedParams() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem", Integer.class,
                String.class);
        initParameters(handlerMethod);

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following query parameters were not documented: [type, text]");

        new RequestParametersSnippet().failOnUndocumentedParams(true).document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void pageRequest_noParams() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem3", Pageable.class);
        initParameters(handlerMethod);

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).isEqualTo(paginationPrefix());

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void pageRequest_withParams() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("searchItem4", int.class, Pageable.class);
        initParameters(handlerMethod);
        mockParamComment("searchItem4", "text", "A text");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithPrefix(paginationPrefix(),
                        tableWithHeader("Parameter", "Type", "Optional", "Description")
                                .row("text", "Integer", "false", "A text.")));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void deprecated() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("removeItem", int.class);
        initParameters(handlerMethod);
        mockParamComment("removeItem", "index", "item's index");

        assertThat(this.generatedSnippets.snippet(AUTO_REQUEST_PARAMETERS)).is(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("index", "Integer", "false", "**Deprecated.**\n\nItem's index."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    private void initParameters(HandlerMethod handlerMethod) {
        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
            parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        }
    }

    private void mockParamComment(String methodName, String parameterName, String comment) {
        when(javadocReader.resolveMethodParameterComment(TestResource.class, methodName,
                parameterName)).thenReturn(comment);
    }

    private void mockConstraintMessage(MethodParameter param, String comment) {
        when(constraintReader.getConstraintMessages(param))
                .thenReturn(singletonList(comment));
    }

    private HandlerMethod createHandlerMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return new HandlerMethod(new TestResource(), name, parameterTypes);
    }

    private String paginationPrefix() {
        if ("adoc".equals(templateFormat.getFileExtension())) {
            return fixLineSeparator("Supports standard <<overview-pagination,paging>> query parameters.\n\n");
        } else {
            return fixLineSeparator("Supports standard [paging](#overview-pagination) query parameters.\n\n");
        }
    }

    private static class TestResource {

        public enum Size {
            SMALL, LARGE
        }

        public void searchItem(
                @RequestParam Integer type,
                @RequestParam(value = "text", required = false) String description) {
            // NOOP
        }

        public void searchItem2(
                @RequestParam double param1,                    // required
                @RequestParam(required = false) boolean param2, // required anyway
                @RequestParam(defaultValue = "1") int param3) { // not required
            // NOOP
        }

        public void searchItem2String(
                @RequestParam double param1,                        // required
                @RequestParam(required = false) boolean param2,     // required anyway
                @RequestParam(defaultValue = "de") String param3) { // not required
            // NOOP
        }

        public void searchItemOptional(
                /**
                 * A nullable Kotlin type is interpreted like a Java type
                 * wrapped in Optional and thus not required.
                 */
                @RequestParam Optional<String> param1) { // not required
            // NOOP
        }

        public void searchItemBySize(@RequestParam Size size) {
            // NOOP
        }

        public void items() {
            // NOOP
        }

        public void searchItem3(Pageable page) {
            // NOOP
        }

        public void searchItem4(@RequestParam int text, Pageable page) {
            // NOOP
        }

        public void searchItem5(@RequestParam Locale locale) {
            // NOOP
        }

        public void removeItem(@Deprecated @RequestParam int index) {
            // NOOP
        }
    }
}
