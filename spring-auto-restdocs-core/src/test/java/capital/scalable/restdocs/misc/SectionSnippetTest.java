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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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

        setField(snippet, "expectedName", "itemsId");
        setField(snippet, "expectedType", "section");
        this.snippet.withContents(equalTo(new StringBuilder()
                .append("[[resources-itemsId]]\n")
                .append("=== Get Item By Id\n\n")
                .append("include::{snippets}/itemsId/method-path.adoc[]\n\n")
                .append("include::{snippets}/itemsId/description.adoc[]\n\n")
                .append("==== Authorization\n\n")
                .append("include::{snippets}/itemsId/authorization.adoc[]\n\n")
                .append("==== Path parameters\n\n")
                .append("include::{snippets}/itemsId/path-parameters.adoc[]\n\n")
                .append("==== Query parameters\n\n")
                .append("include::{snippets}/itemsId/request-parameters.adoc[]\n\n")
                .append("==== Request structure\n\n")
                .append("include::{snippets}/itemsId/request-fields.adoc[]\n\n")
                .append("==== Response structure\n\n")
                .append("include::{snippets}/itemsId/response-fields.adoc[]\n\n")
                .append("==== Example request/response\n\n")
                .append("include::{snippets}/itemsId/curl-request.adoc[]\n")
                .append("include::{snippets}/itemsId/http-response.adoc[]\n")
                .toString()));

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
