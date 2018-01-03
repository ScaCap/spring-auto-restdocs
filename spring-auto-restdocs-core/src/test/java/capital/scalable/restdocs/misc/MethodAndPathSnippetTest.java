package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.REQUEST_PATTERN;
import static capital.scalable.restdocs.misc.MethodAndPathSnippet.METHOD_PATH;
import static org.hamcrest.CoreMatchers.equalTo;

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

        this.snippets.expect(METHOD_PATH).withContents(equalTo("`POST /test`"));

        new MethodAndPathSnippet().document(operationBuilder
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(REQUEST_PATTERN, "/test")
                .request("http://localhost/test")
                .method("POST")
                .build());
    }

    @Test
    public void noHandlerMethod() throws Exception {
        this.snippets.expect(METHOD_PATH).withContents(equalTo("`POST /test`"));

        new MethodAndPathSnippet().document(operationBuilder
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
