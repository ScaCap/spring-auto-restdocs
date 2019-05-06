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
    private JavadocReader javadocReader = mock(JavadocReader.class);

    public DescriptionSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void description() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "testDescription");
        mockMethodComment(TestResource.class, "testDescription", "sample method comment");
        mockMethodTag(TestResource.class, "testDescription", "deprecated", "");

        this.snippets.expect(DESCRIPTION).withContents(equalTo("Sample method comment."));

        new DescriptionSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .request("http://localhost/test")
                .build());
    }

    @Test
    public void descriptionWithDeprecated() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "testDescription");
        mockMethodComment(TestResource.class, "testDescription", "sample method comment");
        mockMethodTag(TestResource.class, "testDescription", "deprecated", "use different one");

        this.snippets.expect(DESCRIPTION).withContents(equalTo(
                "**Deprecated.** Use different one.\n\nSample method comment."));

        new DescriptionSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .request("http://localhost/test")
                .build());
    }

    @Test
    public void descriptionWithSeeTag() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "testDescription");
        mockMethodComment(TestResource.class, "testDescription", "sample method comment");
        mockMethodTag(TestResource.class, "testDescription", "see", "something");

        this.snippets.expect(DESCRIPTION).withContents(equalTo(
                "Sample method comment.\n\nSee something."));

        new DescriptionSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .request("http://localhost/test")
                .build());
    }

    @Test
    public void noHandlerMethod() throws Exception {
        this.snippets.expect(DESCRIPTION).withContents(equalTo(""));

        new DescriptionSnippet().document(operationBuilder
                .request("http://localhost/test")
                .build());
    }

    private void mockMethodComment(Class<TestResource> javaBaseClass, String methodName,
            String comment) {
        when(javadocReader.resolveMethodComment(javaBaseClass, methodName))
                .thenReturn(comment);
    }

    private void mockMethodTag(Class<TestResource> javaBaseClass, String methodName, String tagName,
            String comment) {
        when(javadocReader.resolveMethodTag(javaBaseClass, methodName, tagName))
                .thenReturn(comment);
    }

    private static class TestResource {

        public void testDescription() {
            // NOOP
        }
    }
}
