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

package capital.scalable.restdocs.section;

import static capital.scalable.restdocs.AutoDocumentation.authorization;
import static capital.scalable.restdocs.AutoDocumentation.pathParameters;
import static capital.scalable.restdocs.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.AutoDocumentation.requestParameters;
import static capital.scalable.restdocs.AutoDocumentation.responseFields;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_REQUEST;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_RESPONSE;
import static capital.scalable.restdocs.SnippetRegistry.RESPONSE_FIELDS;
import static capital.scalable.restdocs.section.SectionSnippet.SECTION;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.generate.RestDocumentationGenerator
        .ATTRIBUTE_NAME_DEFAULT_SNIPPETS;

import java.util.ArrayList;
import java.util.Arrays;

import capital.scalable.restdocs.javadoc.JavadocReader;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippetTest extends AbstractSnippetTests {

    private JavadocReader javadocReader;

    public SectionSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Before
    public void setup() {
        javadocReader = mock(JavadocReader.class);
    }

    @Test
    public void noSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-noSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/noSnippets/auto-method-path.adoc[]\n\n" +
                        "include::{snippets}/noSnippets/auto-description.adoc[]\n"));

        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());
    }

    @Test
    public void noHandlerMethod() throws Exception {
        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-noHandlerMethod]]\n" +
                        "=== No Handler Method\n\n" +
                        "include::{snippets}/noHandlerMethod/auto-method-path.adoc[]\n\n" +
                        "include::{snippets}/noHandlerMethod/auto-description.adoc[]\n"));

        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());
    }

    @Test
    public void defaultSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-defaultSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/defaultSnippets/auto-method-path.adoc[]\n\n" +
                        "include::{snippets}/defaultSnippets/auto-description.adoc[]\n\n" +
                        "==== Authorization\n\n" +
                        "include::{snippets}/defaultSnippets/auto-authorization.adoc[]\n\n" +
                        "==== Path parameters\n\n" +
                        "include::{snippets}/defaultSnippets/auto-path-parameters.adoc[]\n\n" +
                        "==== Query parameters\n\n" +
                        "include::{snippets}/defaultSnippets/auto-request-parameters.adoc[]\n\n" +
                        "==== Request fields\n\n" +
                        "include::{snippets}/defaultSnippets/auto-request-fields.adoc[]\n\n" +
                        "==== Response fields\n\n" +
                        "include::{snippets}/defaultSnippets/auto-response-fields.adoc[]\n\n" +
                        "==== Example request\n\n" +
                        "include::{snippets}/defaultSnippets/curl-request.adoc[]\n\n" +
                        "==== Example response\n\n" +
                        "include::{snippets}/defaultSnippets/http-response.adoc[]\n"));

        new SectionBuilder().build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, Arrays.asList(
                                authorization("Public"), pathParameters(), requestParameters(),
                                requestFields(), responseFields(), curlRequest(),
                                HttpDocumentation.httpResponse()))
                        .request("http://localhost/items/1")
                        .build());
    }

    @Test
    public void customSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-customSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/customSnippets/auto-method-path.adoc[]\n\n" +
                        "include::{snippets}/customSnippets/auto-description.adoc[]\n\n" +
                        "==== Example response\n\n" +
                        "include::{snippets}/customSnippets/http-response.adoc[]\n\n" +
                        "==== Response fields\n\n" +
                        "include::{snippets}/customSnippets/auto-response-fields.adoc[]\n\n" +
                        "==== Example request\n\n" +
                        "include::{snippets}/customSnippets/http-request.adoc[]\n"));

        new SectionBuilder()
                .snippetNames(HTTP_RESPONSE, RESPONSE_FIELDS, HTTP_REQUEST)
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, Arrays.asList(
                                pathParameters(), requestParameters(),
                                requestFields(), responseFields(), curlRequest(),
                                HttpDocumentation.httpRequest(), HttpDocumentation.httpResponse()))
                        .request("http://localhost/items/1")
                        .build());
    }

    @Test
    public void skipEmpty() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-skipEmpty]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/skipEmpty/auto-method-path.adoc[]\n\n" +
                        "include::{snippets}/skipEmpty/auto-description.adoc[]\n\n" +
                        "==== Authorization\n\n" +
                        "include::{snippets}/skipEmpty/auto-authorization.adoc[]\n\n" +
                        "==== Example request\n\n" +
                        "include::{snippets}/skipEmpty/curl-request.adoc[]\n\n" +
                        "==== Example response\n\n" +
                        "include::{snippets}/skipEmpty/http-response.adoc[]\n"));

        new SectionBuilder()
                .skipEmpty(true)
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, Arrays.asList(
                                authorization("Public"), pathParameters(), requestParameters(),
                                requestFields(), responseFields(), curlRequest(),
                                HttpDocumentation.httpResponse()))
                        .request("http://localhost/items/1")
                        .build());
    }

    @Test
    public void customTitle() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "Custom title");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-customTitle]]\n" +
                        "=== Custom title\n\n" +
                        "include::{snippets}/customTitle/auto-method-path.adoc[]\n\n" +
                        "include::{snippets}/customTitle/auto-description.adoc[]\n"));

        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());
    }


    private void mockMethodTitle(Class<?> javaBaseClass, String methodName, String title) {
        when(javadocReader.resolveMethodTitle(javaBaseClass, methodName))
                .thenReturn(title);
    }

    private static class TestResource {


        public void getItemById() {
            // NOOP
        }
    }
}
