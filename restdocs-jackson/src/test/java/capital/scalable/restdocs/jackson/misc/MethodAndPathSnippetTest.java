package capital.scalable.restdocs.jackson.misc;

import static capital.scalable.restdocs.jackson.OperationAttributeHelper.REQUEST_PATTERN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.method.HandlerMethod;

public class MethodAndPathSnippetTest extends AbstractSnippetTests {

    public MethodAndPathSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(), "testMethod");

        setField(snippet, "expectedName", "method-path");
        setField(snippet, "expectedType", "method-path");
        this.snippet.withContents(equalTo("`POST /test`"));

        new MethodAndPathSnippet().document(operationBuilder("method-path")
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(REQUEST_PATTERN, "/test")
                .request("http://localhost/test")
                .method("POST")
                .build());
    }

    private static class TestResource {

        public void testMethod() {
            // NOOP
        }
    }
}
