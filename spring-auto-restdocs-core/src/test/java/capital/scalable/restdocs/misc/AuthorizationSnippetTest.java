package capital.scalable.restdocs.misc;


import static capital.scalable.restdocs.misc.AuthorizationSnippet.AUTHORIZATION;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;

public class AuthorizationSnippetTest extends AbstractSnippetTests {

    public AuthorizationSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void authorization() throws Exception {
        String authorization = "User access token required.";

        this.snippets.expect(AUTHORIZATION)
                .withContents(equalTo(authorization));

        new AuthorizationSnippet("Resource is public.")
                .document(operationBuilder
                        .attribute(AuthorizationSnippet.class.getName(), authorization)
                        .request("http://localhost/test")
                        .build());
    }

    @Test
    public void defaultAuthorization() throws Exception {
        String defaultAuthorization = "Resource is public.";

        this.snippets.expect(AUTHORIZATION)
                .withContents(equalTo(defaultAuthorization));

        new AuthorizationSnippet(defaultAuthorization)
                .document(operationBuilder
                        .request("http://localhost/test")
                        .build());
    }
}
