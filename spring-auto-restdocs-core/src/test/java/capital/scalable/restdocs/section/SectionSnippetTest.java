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
package capital.scalable.restdocs.section;

import static capital.scalable.restdocs.AutoDocumentation.authorization;
import static capital.scalable.restdocs.AutoDocumentation.pathParameters;
import static capital.scalable.restdocs.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.AutoDocumentation.requestParameters;
import static capital.scalable.restdocs.AutoDocumentation.responseFields;
import static capital.scalable.restdocs.SnippetRegistry.AUTO_RESPONSE_FIELDS;
import static capital.scalable.restdocs.SnippetRegistry.CURL_REQUEST;
import static capital.scalable.restdocs.SnippetRegistry.HTTPIE_REQUEST;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_REQUEST;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_RESPONSE;
import static capital.scalable.restdocs.SnippetRegistry.LINKS;
import static capital.scalable.restdocs.SnippetRegistry.PATH_PARAMETERS;
import static capital.scalable.restdocs.SnippetRegistry.REQUEST_BODY_SNIPPET;
import static capital.scalable.restdocs.SnippetRegistry.REQUEST_FIELDS;
import static capital.scalable.restdocs.SnippetRegistry.REQUEST_HEADERS;
import static capital.scalable.restdocs.SnippetRegistry.REQUEST_PARAMETERS;
import static capital.scalable.restdocs.SnippetRegistry.REQUEST_PARTS;
import static capital.scalable.restdocs.SnippetRegistry.REQUEST_PART_FIELDS;
import static capital.scalable.restdocs.SnippetRegistry.RESPONSE_BODY;
import static capital.scalable.restdocs.SnippetRegistry.RESPONSE_FIELDS;
import static capital.scalable.restdocs.SnippetRegistry.RESPONSE_HEADERS;
import static capital.scalable.restdocs.section.SectionSnippet.SECTION;
import static capital.scalable.restdocs.util.FormatUtil.fixLineSeparator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.generate.RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS;

import java.util.ArrayList;
import java.util.Arrays;

import capital.scalable.restdocs.i18n.TranslationRule;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.GeneratedSnippets;
import org.springframework.restdocs.test.OperationBuilder;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippetTest {

    private JavadocReader javadocReader;

    @Rule
    public TranslationRule translationRule = new TranslationRule();

    @Rule
    public GeneratedSnippets generatedSnippets;

    @Rule
    public OperationBuilder operationBuilder;

    public SectionSnippetTest() {
        // Only runs for AsciiDoctor, because Markdown is not supported.
        TemplateFormat templateFormat = TemplateFormats.asciidoctor();
        this.generatedSnippets = new GeneratedSnippets(templateFormat);
        this.operationBuilder = new OperationBuilder(templateFormat);
    }

    @Before
    public void setup() {
        javadocReader = mock(JavadocReader.class);
    }

    @Test
    public void noSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-noSnippets]]\n" +
                                "=== Get Item By Id\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n"));
    }

    @Test
    public void noHandlerMethod() throws Exception {
        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-noHandlerMethod]]\n" +
                                "=== No Handler Method\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n"));
    }

    @Test
    public void defaultSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

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

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-defaultSnippets]]\n" +
                                "=== Get Item By Id\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n\n" +
                                "==== Authorization\n\n" +
                                "include::auto-authorization.adoc[]\n\n" +
                                "==== Path parameters\n\n" +
                                "include::auto-path-parameters.adoc[]\n\n" +
                                "==== Query parameters\n\n" +
                                "include::auto-request-parameters.adoc[]\n\n" +
                                "==== Request fields\n\n" +
                                "include::auto-request-fields.adoc[]\n\n" +
                                "==== Response fields\n\n" +
                                "include::auto-response-fields.adoc[]\n\n" +
                                "==== Example request\n\n" +
                                "include::curl-request.adoc[]\n\n" +
                                "==== Example response\n\n" +
                                "include::http-response.adoc[]\n"));
    }

    @Test
    public void customSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        new SectionBuilder()
                .snippetNames(HTTP_RESPONSE, AUTO_RESPONSE_FIELDS, HTTP_REQUEST)
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

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-customSnippets]]\n" +
                                "=== Get Item By Id\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n\n" +
                                "==== Example response\n\n" +
                                "include::http-response.adoc[]\n\n" +
                                "==== Response fields\n\n" +
                                "include::auto-response-fields.adoc[]\n\n" +
                                "==== Example request\n\n" +
                                "include::http-request.adoc[]\n"));
    }

    @Test
    public void allClassicSnippets() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        new SectionBuilder()
                .snippetNames(CURL_REQUEST, HTTP_REQUEST, HTTP_RESPONSE, HTTPIE_REQUEST, PATH_PARAMETERS,
                        REQUEST_PARAMETERS, REQUEST_FIELDS, RESPONSE_FIELDS, REQUEST_BODY_SNIPPET, RESPONSE_BODY,
                        REQUEST_HEADERS, RESPONSE_HEADERS, REQUEST_PARTS, REQUEST_PART_FIELDS, LINKS)
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

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-allClassicSnippets]]\n"
                                + "=== Get Item By Id\n\n"
                                + "include::auto-method-path.adoc[]\n\n"
                                + "include::auto-description.adoc[]\n\n"
                                + "==== Example request\n\n"
                                + "include::curl-request.adoc[]\n\n"
                                + "==== Example request\n\n"
                                + "include::http-request.adoc[]\n\n"
                                + "==== Example response\n\n"
                                + "include::http-response.adoc[]\n\n"
                                + "==== Example request\n\n"
                                + "include::httpie-request.adoc[]\n\n"
                                + "==== Path parameters\n\n"
                                + "include::path-parameters.adoc[]\n\n"
                                + "==== Query parameters\n\n"
                                + "include::request-parameters.adoc[]\n\n"
                                + "==== Request fields\n\n"
                                + "include::request-fields.adoc[]\n\n"
                                + "==== Response fields\n\n"
                                + "include::response-fields.adoc[]\n\n"
                                + "==== Request Body\n\n"
                                + "include::request-body.adoc[]\n\n"
                                + "==== Response Body\n\n"
                                + "include::response-body.adoc[]\n\n"
                                + "==== Request headers\n\n"
                                + "include::request-headers.adoc[]\n\n"
                                + "==== Response headers\n\n"
                                + "include::response-headers.adoc[]\n\n"
                                + "==== Request Parts\n\n"
                                + "include::request-parts.adoc[]\n\n"
                                + "==== Request Part Fields\n\n"
                                + "include::request-part-fields.adoc[]\n\n"
                                + "==== Hypermedia links\n\n"
                                + "include::links.adoc[]\n"));
    }

    @Test
    public void skipEmpty() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

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

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-skipEmpty]]\n" +
                                "=== Get Item By Id\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n\n" +
                                "==== Authorization\n\n" +
                                "include::auto-authorization.adoc[]\n\n" +
                                "==== Example request\n\n" +
                                "include::curl-request.adoc[]\n\n" +
                                "==== Example response\n\n" +
                                "include::http-response.adoc[]\n"));
    }

    @Test
    public void customTitle() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "Custom title");

        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-customTitle]]\n" +
                                "=== Custom title\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n"));
    }

    @Test
    public void deprecated() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");
        when(javadocReader.resolveMethodTag(TestResource.class, "getItemById", "deprecated"))
                .thenReturn("it is");

        new SectionBuilder()
                .snippetNames()
                .build()
                .document(operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ATTRIBUTE_NAME_DEFAULT_SNIPPETS, new ArrayList<>())
                        .request("http://localhost/items/1")
                        .build());

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-deprecated]]\n" +
                                "=== Get Item By Id (deprecated)\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n"));
    }

    @Test
    public void translation() throws Exception {
        translationRule.setTestTranslations();

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

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

        assertThat(this.generatedSnippets.snippet(SECTION))
                .isEqualTo(fixLineSeparator(
                        "[[resources-translation]]\n" +
                                "=== Get Item By Id\n\n" +
                                "include::auto-method-path.adoc[]\n\n" +
                                "include::auto-description.adoc[]\n\n" +
                                "==== XAuthorization\n\n" +
                                "include::auto-authorization.adoc[]\n\n" +
                                "==== XPath parameters\n\n" +
                                "include::auto-path-parameters.adoc[]\n\n" +
                                "==== XQuery parameters\n\n" +
                                "include::auto-request-parameters.adoc[]\n\n" +
                                "==== XRequest fields\n\n" +
                                "include::auto-request-fields.adoc[]\n\n" +
                                "==== XResponse fields\n\n" +
                                "include::auto-response-fields.adoc[]\n\n" +
                                "==== XExample request\n\n" +
                                "include::curl-request.adoc[]\n\n" +
                                "==== XExample response\n\n" +
                                "include::http-response.adoc[]\n"));
    }

    private void mockMethodTitle(Class<?> javaBaseClass, String methodName, String title) {
        when(javadocReader.resolveMethodTag(javaBaseClass, methodName, "title"))
                .thenReturn(title);
    }

    private static class TestResource {

        public void getItemById() {
            // NOOP
        }
    }
}
