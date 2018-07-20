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
package capital.scalable.restdocs.section;

import static capital.scalable.restdocs.AutoDocumentation.authorization;
import static capital.scalable.restdocs.AutoDocumentation.pathParameters;
import static capital.scalable.restdocs.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.AutoDocumentation.requestParameters;
import static capital.scalable.restdocs.AutoDocumentation.responseFields;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_REQUEST;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_RESPONSE;
import static capital.scalable.restdocs.SnippetRegistry.LINKS;
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
import java.util.Collections;
import java.util.List;

import capital.scalable.restdocs.SnippetRegistry;
import capital.scalable.restdocs.i18n.TranslationRule;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.ExpectedSnippets;
import org.springframework.restdocs.test.OperationBuilder;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippetTest {

    private JavadocReader javadocReader;

    @Rule
    public TranslationRule translationRule = new TranslationRule();

    @Rule
    public ExpectedSnippets snippets;

    @Rule
    public OperationBuilder operationBuilder;

    public SectionSnippetTest() {
        // Only runs for AsciiDoctor, because Markdown is not supported.
        TemplateFormat templateFormat = TemplateFormats.asciidoctor();
        this.snippets = new ExpectedSnippets(templateFormat);
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

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-noSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n"));

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
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n"));

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
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n\n" +
                        "==== Example response\n\n" +
                        "include::http-response.adoc[]\n\n" +
                        "==== Response fields\n\n" +
                        "include::auto-response-fields.adoc[]\n\n" +
                        "==== Example request\n\n" +
                        "include::http-request.adoc[]\n"));

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
    public void additionalClassicSnippets() throws Exception {
        translationRule.setTestTranslations();
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");
        SnippetRegistry.addClassicSnippet(new SectionSupport() {
            @Override
            public String getFileName() {
                return "my-snippet-name";
            }

            @Override
            public String getHeaderKey() {
                return "my-snippet-header-key";
            }

            @Override
            public boolean hasContent(Operation operation) {
                return true;
            }
        });

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-additionalClassicSnippets]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n\n" +
                        "==== XExample response\n\n" +
                        "include::http-response.adoc[]\n\n" +
                        "==== XResponse fields\n\n" +
                        "include::auto-response-fields.adoc[]\n\n" +
                        "==== XExample request\n\n" +
                        "include::http-request.adoc[]\n\n" +
                        "==== My snippet\n\n" +
                        "include::my-snippet-name.adoc[]\n"));

        new SectionBuilder()
                .snippetNames(HTTP_RESPONSE, RESPONSE_FIELDS, HTTP_REQUEST, "my-snippet-name")
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
    public void linkSnippet() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-linkSnippet]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n\n" +
                        "==== Hypermedia links\n\n" +
                        "include::links.adoc[]\n"));

        new SectionBuilder()
                .snippetNames(LINKS)
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
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n\n" +
                        "==== Authorization\n\n" +
                        "include::auto-authorization.adoc[]\n\n" +
                        "==== Example request\n\n" +
                        "include::curl-request.adoc[]\n\n" +
                        "==== Example response\n\n" +
                        "include::http-response.adoc[]\n"));

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
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n"));

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
    public void deprecated() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");
        when(javadocReader.resolveMethodTag(TestResource.class, "getItemById", "deprecated"))
                .thenReturn("it is");


        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-deprecated]]\n" +
                        "=== Get Item By Id (deprecated)\n\n" +
                        "include::auto-method-path.adoc[]\n\n" +
                        "include::auto-description.adoc[]\n"));

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
    public void translation() throws Exception {
        translationRule.setTestTranslations();

        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-translation]]\n" +
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
