/*
 * Copyright 2017 the original author or authors.
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

package capital.scalable.restdocs.request;


import static capital.scalable.restdocs.request.RequestHeaderSnippet.REQUEST_HEADERS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.method.HandlerMethod;

public class RequestHeaderSnippetTest extends AbstractSnippetTests {

    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public RequestHeaderSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Before
    public void setup() {
        javadocReader = mock(JavadocReader.class);
        constraintReader = mock(ConstraintReader.class);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("updateItem", Integer.class, String.class,
                int.class, String.class);
        initParameters(handlerMethod);
        mockParamComment("updateItem", "id", "An integer");
        mockParamComment("updateItem", "otherId", "A string");
        mockParamComment("updateItem", "partId", "An integer");
        mockParamComment("updateItem", "yetAnotherId", "A string");

        this.snippets.expect(REQUEST_HEADERS).withContents(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("id", "Integer", "false", "An integer.")
                        .row("subId", "String", "false", "A string.")
                        .row("partId", "Integer", "false", "An integer.")
                        .row("yetAnotherId", "String", "true", "A string."));

        new RequestHeaderSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void noHeaders() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("updateItem");

        this.snippets.expect(REQUEST_HEADERS).withContents(equalTo("No headers."));

        new RequestHeaderSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
    }

    @Test
    public void noHandlerMethod() throws Exception {
        this.snippets.expect(REQUEST_HEADERS).withContents(equalTo("No headers."));

        new RequestHeaderSnippet().document(operationBuilder
                .build());
    }

    @Test
    public void failOnUndocumentedHeaders() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("updateItem", Integer.class, String.class,
                int.class, String.class);
        initParameters(handlerMethod);

        thrown.expect(SnippetException.class);
        thrown.expectMessage(
                "Following request headers were not documented: [id, subId, partId, yetAnotherId]");

        new RequestHeaderSnippet().failOnUndocumentedParams(true).document(operationBuilder
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

        this.snippets.expect(REQUEST_HEADERS).withContents(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("index", "Integer", "false", "**Deprecated.**\n\nItem's index."));

        new RequestHeaderSnippet().document(operationBuilder
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

    private HandlerMethod createHandlerMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return new HandlerMethod(new TestResource(), name, parameterTypes);
    }

    private static class TestResource {

        @GetMapping("/items")
        public void updateItem(@RequestHeader Integer id,
                @RequestHeader("subId") String otherId,
                // partId is required anyway, because it's a primitive type
                @RequestHeader(required = false) int partId,
                @RequestHeader(required = false) String yetAnotherId) {
            // NOOP
        }

        @PostMapping("/items")
        public void updateItem() {
            // NOOP
        }

        public void removeItem(@Deprecated @RequestHeader int index) {
        }
    }
}
