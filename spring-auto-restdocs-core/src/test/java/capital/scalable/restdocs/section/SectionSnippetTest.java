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
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.generate.RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippetTest extends AbstractSnippetTests {

    public SectionSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void noSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-noSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/noSnippets/method-path.adoc[]\n\n" +
                        "include::{snippets}/noSnippets/description.adoc[]\n"));

        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());
    }

    @Test
    public void defaultSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-defaultSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/defaultSnippets/method-path.adoc[]\n\n" +
                        "include::{snippets}/defaultSnippets/description.adoc[]\n\n" +
                        "==== Authorization\n\n" +
                        "include::{snippets}/defaultSnippets/authorization.adoc[]\n\n" +
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

        new SectionBuilder()
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
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

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-customSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/customSnippets/method-path.adoc[]\n\n" +
                        "include::{snippets}/customSnippets/description.adoc[]\n\n" +
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

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-skipEmpty]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/skipEmpty/method-path.adoc[]\n\n" +
                        "include::{snippets}/skipEmpty/description.adoc[]\n\n" +
                        "==== Authorization\n\n" +
                        "include::{snippets}/skipEmpty/authorization.adoc[]\n\n" +
                        "==== Example request\n\n" +
                        "include::{snippets}/skipEmpty/curl-request.adoc[]\n\n" +
                        "==== Example response\n\n" +
                        "include::{snippets}/skipEmpty/http-response.adoc[]\n"));

        new SectionBuilder()
                .skipEmpty(true)
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, Arrays.asList(
                                authorization("Public"), pathParameters(), requestParameters(),
                                requestFields(), responseFields(), curlRequest(),
                                HttpDocumentation.httpResponse()))
                        .request("http://localhost/items/1")
                        .build());
    }

    private static class TestResource {

        public void getItemById() {
            // NOOP
        }
    }
}
