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

package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.misc.SectionSnippet.SECTION;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippetTest extends AbstractSnippetTests {

    public SectionSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void itemsId() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "getItemById");

        this.snippets.expect(SECTION)
                .withContents(equalTo("[[resources-itemsId]]\n" +
                        "=== Get Item By Id\n\n" +
                        "include::{snippets}/itemsId/method-path.adoc[]\n\n" +
                        "include::{snippets}/itemsId/description.adoc[]\n\n" +
                        "==== Authorization\n\n" +
                        "include::{snippets}/itemsId/authorization.adoc[]\n\n" +
                        "==== Path parameters\n\n" +
                        "include::{snippets}/itemsId/path-parameters.adoc[]\n\n" +
                        "==== Query parameters\n\n" +
                        "include::{snippets}/itemsId/request-parameters" +
                        ".adoc[]\n\n" +
                        "==== Request structure\n\n" +
                        "include::{snippets}/itemsId/request-fields.adoc[]\n\n" +
                        "==== Response structure\n\n" +
                        "include::{snippets}/itemsId/response-fields.adoc[]\n\n" +
                        "==== Example request/response\n\n" +
                        "include::{snippets}/itemsId/curl-request.adoc[]\n" +
                        "include::{snippets}/itemsId/http-response.adoc[]\n"));

        new SectionSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .request("http://localhost/items/1")
                .build());
    }

    private static class TestResource {

        public void getItemById() {
            // NOOP
        }
    }
}
