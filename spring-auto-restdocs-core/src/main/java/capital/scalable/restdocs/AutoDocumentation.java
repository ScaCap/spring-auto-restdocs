package capital.scalable.restdocs;

import capital.scalable.restdocs.misc.AuthorizationSnippet;
import capital.scalable.restdocs.misc.DescriptionSnippet;
import capital.scalable.restdocs.misc.MethodAndPathSnippet;
import capital.scalable.restdocs.payload.JacksonRequestFieldSnippet;
import capital.scalable.restdocs.payload.JacksonResponseFieldSnippet;
import capital.scalable.restdocs.request.PathParametersSnippet;
import capital.scalable.restdocs.request.RequestHeaderSnippet;
import capital.scalable.restdocs.request.RequestParametersSnippet;
import capital.scalable.restdocs.section.SectionBuilder;
import org.springframework.restdocs.snippet.Snippet;

public abstract class AutoDocumentation {

    private AutoDocumentation() {
    }

    public static JacksonRequestFieldSnippet requestFields() {
        return new JacksonRequestFieldSnippet();
    }

    public static JacksonResponseFieldSnippet responseFields() {
        return new JacksonResponseFieldSnippet();
    }

    public static Snippet pathParameters() {
        return new PathParametersSnippet();
    }

    public static Snippet requestHeaders() {
        return new RequestHeaderSnippet();
    }

    public static Snippet requestParameters() {
        return new RequestParametersSnippet();
    }

    public static Snippet description() {
        return new DescriptionSnippet();
    }

    public static Snippet methodAndPath() {
        return new MethodAndPathSnippet();
    }

    public static Snippet section() {
        return new SectionBuilder().build();
    }

    public static SectionBuilder sectionBuilder() {
        return new SectionBuilder();
    }

    public static Snippet authorization(String defaultAuthorization) {
        return new AuthorizationSnippet(defaultAuthorization);
    }
}
