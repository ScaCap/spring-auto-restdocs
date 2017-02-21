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
import org.junit.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

public class RequestParametersSnippetTest extends AbstractSnippetTests {

    public RequestParametersSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(),
                "searchItem", Integer.class, String.class);
        initParameters(handlerMethod);

        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader
                .resolveMethodParameterComment(TestResource.class, "searchItem", "type"))
                .thenReturn("An integer");
        when(javadocReader
                .resolveMethodParameterComment(TestResource.class, "searchItem", "description"))
                .thenReturn("A string");

        ConstraintReader constraintReader = mock(ConstraintReader.class);

        this.snippets.expectRequestParameters().withContents(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("type", "Integer", "false", "An integer")
                        .row("text", "String", "true", "A string"));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .request("http://localhost/items/search?type=main&text=myItem")
                .build());
    }

    @Test
    public void noParameters() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "items");

        this.snippets.expectRequestParameters().withContents(equalTo("No parameters."));

        new RequestParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .request("http://localhost/items")
                .build());
    }

    private void initParameters(HandlerMethod handlerMethod) {
        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
            parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        }
    }

    private static class TestResource {

        @RequestMapping(value = "/items/search")
        public void searchItem(@RequestParam Integer type,
                @RequestParam(value = "text", required = false) String description) {
            // NOOP
        }

        public void items() {
            // NOOP
        }
    }
}
