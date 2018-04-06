/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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


import static capital.scalable.restdocs.request.PathParametersSnippet.PATH_PARAMETERS;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.HandlerMethod;

public class PathParametersSnippetTest extends AbstractSnippetTests {

    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public PathParametersSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Before
    public void setup() {
        javadocReader = mock(JavadocReader.class);
        constraintReader = mock(ConstraintReader.class);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem", Integer.class, String.class,
                int.class, String.class, Optional.class);
        initParameters(handlerMethod);
        mockParamComment("addItem", "id", "An integer");
        mockParamComment("addItem", "otherId", "A string");
        mockParamComment("addItem", "partId", "An integer");
        mockParamComment("addItem", "yetAnotherId", "Another string");
        mockParamComment("addItem", "optionalId", "Optional string");

        this.snippets.expect(PATH_PARAMETERS).withContents(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("id", "Integer", "false", "An integer.")
                        .row("subid", "String", "false", "A string.")
                        .row("partId", "Integer", "false", "An integer.")
                        .row("yetAnotherId", "String", "true", "Another string.")
                        .row("optionalId", "String", "true", "Optional string."));

        new PathParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void simpleRequestWithEnum() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("getItemsByShape",
                TestResource.Shape.class);
        initParameters(handlerMethod);
        mockParamComment("getItemsByShape", "shape", "An enum");
        mockConstraintMessage(handlerMethod.getMethodParameters()[0],
                "Must be one of [SPHERIC, SQUARE]");

        this.snippets.expect(PATH_PARAMETERS).withContents(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("shape", "String", "false",
                                "An enum.\n\nMust be one of [SPHERIC, SQUARE]."));

        new PathParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());
    }

    @Test
    public void noParameters() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem");

        this.snippets.expect(PATH_PARAMETERS).withContents(equalTo("No parameters."));

        new PathParametersSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
    }

    @Test
    public void noHandlerMethod() throws Exception {
        this.snippets.expect(PATH_PARAMETERS).withContents(equalTo("No parameters."));

        new PathParametersSnippet().document(operationBuilder
                .build());
    }

    @Test
    public void failOnUndocumentedParams() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem", Integer.class, String.class,
                int.class, String.class, Optional.class);
        initParameters(handlerMethod);

        thrown.expect(SnippetException.class);
        thrown.expectMessage(
                "Following path parameters were not documented: [id, subid, partId, yetAnotherId, optionalId]");

        new PathParametersSnippet().failOnUndocumentedParams(true).document(operationBuilder
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

        this.snippets.expect(PATH_PARAMETERS).withContents(
                tableWithHeader("Parameter", "Type", "Optional", "Description")
                        .row("index", "Integer", "false", "**Deprecated.**\n\nItem's index."));

        new PathParametersSnippet().document(operationBuilder
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

    private static class TestResource {

        enum Shape {
            SPHERIC, SQUARE
        }

        @PostMapping("/items/{id}/subitem/{subid}/{partId}/{yetAnotherId}/{optionalId}")
        public void addItem(@PathVariable Integer id,
                @PathVariable("subid") String otherId,
                // partId is required anyway, because it's a primitive type
                @PathVariable(required = false) int partId,
                @PathVariable(required = false) String yetAnotherId,
                @PathVariable Optional<String> optionalId) {
            // NOOP
        }

        @GetMapping("/items")
        public void addItem() {
            // NOOP
        }

        @GetMapping("/items/{shape}")
        public void getItemsByShape(@PathVariable Shape shape) {
            // NOOP
        }

        public void removeItem(@Deprecated @PathVariable int index) {
        }
    }
}
