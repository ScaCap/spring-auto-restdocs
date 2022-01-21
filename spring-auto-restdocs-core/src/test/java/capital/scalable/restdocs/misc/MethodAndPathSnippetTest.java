/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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
package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.REQUEST_PATTERN;
import static capital.scalable.restdocs.SnippetRegistry.AUTO_METHOD_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import capital.scalable.restdocs.AbstractSnippetTests;
import org.junit.Test;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.web.method.HandlerMethod;

public class MethodAndPathSnippetTest extends AbstractSnippetTests {

    public MethodAndPathSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "testMethod");

        new MethodAndPathSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(REQUEST_PATTERN, "/test")
                .request("http://localhost/test")
                .method("POST")
                .build());

        if (TemplateFormats.asciidoctor().equals(this.templateFormat)) {
            assertThat(this.generatedSnippets.snippet(AUTO_METHOD_PATH)).isEqualTo("`+POST /test+`");
        } else {
            assertThat(this.generatedSnippets.snippet(AUTO_METHOD_PATH)).isEqualTo("`POST /test`");
        }
    }

    @Test
    public void noHandlerMethod() throws Exception {
        new MethodAndPathSnippet().document(operationBuilder
                .attribute(REQUEST_PATTERN, "/test")
                .request("http://localhost/test")
                .method("POST")
                .build());

        if (TemplateFormats.asciidoctor().equals(this.templateFormat)) {
            assertThat(this.generatedSnippets.snippet(AUTO_METHOD_PATH)).isEqualTo("`+POST /test+`");
        } else {
            assertThat(this.generatedSnippets.snippet(AUTO_METHOD_PATH)).isEqualTo("`POST /test`");
        }
    }

    private static class TestResource {

        public void testMethod() {
            // NOOP
        }
    }
}
