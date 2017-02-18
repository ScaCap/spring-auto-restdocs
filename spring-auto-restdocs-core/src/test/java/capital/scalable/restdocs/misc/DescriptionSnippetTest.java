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


import static capital.scalable.restdocs.misc.DescriptionSnippet.DESCRIPTION;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import capital.scalable.restdocs.javadoc.JavadocReader;
import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.method.HandlerMethod;

public class DescriptionSnippetTest extends AbstractSnippetTests {

    public DescriptionSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void description() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(),
                "testDescription");
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveMethodComment(TestResource.class, "testDescription"))
                .thenReturn("Sample method comment");

        this.snippets.expect(DESCRIPTION)
                .withContents(equalTo("Sample method comment"));

        new DescriptionSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .request("http://localhost/test")
                .build());
    }

    private static class TestResource {

        public void testDescription() {
            // NOOP
        }
    }
}
