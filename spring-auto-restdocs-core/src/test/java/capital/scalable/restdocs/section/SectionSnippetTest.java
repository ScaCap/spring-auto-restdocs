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
import static capital.scalable.restdocs.SnippetRegistry.AUTO_RESPONSE_FIELDS;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_REQUEST;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_RESPONSE;
import static capital.scalable.restdocs.section.SectionSnippet.SECTION;
import static org.hamcrest.CoreMatchers.equalTo;
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
import org.springframework.restdocs.test.ExpectedSnippets;
import org.springframework.restdocs.test.OperationBuilder;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippetTest {

    private static final String LINE_SEPERATOR = System.lineSeparator();
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
                .withContents(equalTo("[[resources-noSnippets]]" + LINE_SEPERATOR +
                        "=== Get Item By Id" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR));

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
                .withContents(equalTo("[[resources-noHandlerMethod]]" + LINE_SEPERATOR +
                        "=== No Handler Method" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR));

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
                .withContents(equalTo("[[resources-defaultSnippets]]" + LINE_SEPERATOR +
                        "=== Get Item By Id" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Authorization" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-authorization.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Path parameters" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-path-parameters.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Query parameters" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-request-parameters.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Request fields" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-request-fields.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Response fields" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-response-fields.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Example request" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::curl-request.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Example response" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::http-response.adoc[]" + LINE_SEPERATOR));

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
                .withContents(equalTo("[[resources-customSnippets]]" + LINE_SEPERATOR +
                        "=== Get Item By Id" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Example response" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::http-response.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Response fields" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-response-fields.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Example request" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::http-request.adoc[]" + LINE_SEPERATOR));

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
    }

    @Test
    public void skipEmpty() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");
        mockMethodTitle(TestResource.class, "getItemById", "");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-skipEmpty]]" + LINE_SEPERATOR +
                        "=== Get Item By Id" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Authorization" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-authorization.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Example request" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::curl-request.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== Example response" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::http-response.adoc[]" + LINE_SEPERATOR));

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
                .withContents(equalTo("[[resources-customTitle]]" + LINE_SEPERATOR +
                        "=== Custom title" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR));

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
                .withContents(equalTo("[[resources-deprecated]]" + LINE_SEPERATOR +
                        "=== Get Item By Id (deprecated)" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR));

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
                .withContents(equalTo("[[resources-translation]]" + LINE_SEPERATOR +
                        "=== Get Item By Id" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-method-path.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-description.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== XAuthorization" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-authorization.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== XPath parameters" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-path-parameters.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== XQuery parameters" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-request-parameters.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== XRequest fields" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-request-fields.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== XResponse fields" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::auto-response-fields.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== XExample request" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::curl-request.adoc[]" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "==== XExample response" + LINE_SEPERATOR + LINE_SEPERATOR +
                        "include::http-response.adoc[]" + LINE_SEPERATOR));

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
