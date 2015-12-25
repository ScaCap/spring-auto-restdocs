package capital.scalable.restdocs.jackson.payload;

import org.springframework.restdocs.snippet.Snippet;

public abstract class AutoPayloadDocumentation {

    private AutoPayloadDocumentation() {

    }

    public static Snippet requestFields() {
        return new JacksonRequestFieldSnippet();
    }

    public static Snippet responseFields() {
        return new JacksonResponseFieldSnippet();
    }
}
