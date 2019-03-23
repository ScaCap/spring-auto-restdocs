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
package capital.scalable.restdocs.hypermedia;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_EMBEDDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.method.HandlerMethod;

public class EmbeddedSnippetTest extends AbstractSnippetTests {
    private ObjectMapper mapper;
    private JavadocReader javadocReader;
    private ConstraintReader constraintReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public EmbeddedSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        javadocReader = mock(JavadocReader.class);
        constraintReader = mock(ConstraintReader.class);
    }

    @Test
    public void embedded() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem");
        mockFieldComment(EmbeddedDocs.class, "embedded1", "Resource 1");
        mockFieldComment(EmbeddedDocs.class, "embedded2", "Resource 2");

        new EmbeddedSnippet().documentationType(EmbeddedDocs.class).document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .attribute(JavadocReader.class.getName(), javadocReader)
                .attribute(ConstraintReader.class.getName(), constraintReader)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_EMBEDDED)).is(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("embedded1", "Array[Object]", "true", "Resource 1.")
                        .row("embedded2", "Object", "true", "Resource 2."));
    }

    @Test
    public void noHandlerMethod() throws Exception {
        new EmbeddedSnippet().document(operationBuilder
                .attribute(ObjectMapper.class.getName(), mapper)
                .build());

        assertThat(this.generatedSnippets.snippet(AUTO_EMBEDDED))
                .isEqualTo("No embedded resources.");
    }

    @Test
    public void noContent() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem");

        boolean hasContent = new EmbeddedSnippet().hasContent(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .build());
        assertThat(hasContent).isFalse();
    }

    @Test
    public void failOnUndocumentedFields() throws Exception {
        HandlerMethod handlerMethod = createHandlerMethod("addItem");

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following embedded resources were not documented: [embedded1, embedded2]");

        new EmbeddedSnippet().documentationType(EmbeddedDocs.class).failOnUndocumentedFields(true).document(
                operationBuilder
                        .attribute(HandlerMethod.class.getName(), handlerMethod)
                        .attribute(ObjectMapper.class.getName(), mapper)
                        .attribute(JavadocReader.class.getName(), javadocReader)
                        .attribute(ConstraintReader.class.getName(), constraintReader)
                        .build());
    }

    private void mockFieldComment(Class<?> type, String fieldName, String comment) {
        when(javadocReader.resolveFieldComment(type, fieldName))
                .thenReturn(comment);
    }

    private HandlerMethod createHandlerMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return new HandlerMethod(new TestResource(), name, parameterTypes);
    }

    private static class TestResource {

        public void addItem() {
            // NOOP
        }
    }

    private static class EmbeddedDocs {
        private List<EmbeddedDocs> embedded1;
        private EmbeddedDocs embedded2;
    }
}
