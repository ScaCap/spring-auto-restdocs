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

package capital.scalable.restdocs.request;


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
import org.springframework.web.bind.annotation.RequestMapping;
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

        this.snippets.expectRequestParameters().withContents(
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

        this.snippets.expectRequestParameters().withContents(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("param1", "Decimal", "false", "A decimal.")
                        .row("param2", "Boolean", "false", "A boolean.")
                        .row("param3", "Integer", "true", "An integer."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void noParameters() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("items");

        this.snippets.expectRequestParameters().withContents(equalTo("No parameters."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
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

        @RequestMapping(value = "/items/search")
        public void searchItem(@RequestParam Integer type,
                @RequestParam(value = "text", required = false) String description) {
            // NOOP
        }

        @RequestMapping(value = "/items/search2")
        public void searchItem2(@RequestParam double param1,    // required
                @RequestParam(required = false) boolean param2, // required anyway
                @RequestParam(defaultValue = "1") int param3) { // not required
            // NOOP
        }

        public void items() {
            // NOOP
        }
    }
}
