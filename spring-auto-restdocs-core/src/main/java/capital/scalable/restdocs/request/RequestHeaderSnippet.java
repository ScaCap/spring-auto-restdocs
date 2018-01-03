package capital.scalable.restdocs.request;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestHeader;

public class RequestHeaderSnippet extends AbstractParameterSnippet<RequestHeader> {

    public static final String REQUEST_HEADERS = "auto-request-headers";
    private final boolean failOnUndocumentedParams;

    public RequestHeaderSnippet() {
        this(false);
    }

    public RequestHeaderSnippet(boolean failOnUndocumentedParams) {
        super(REQUEST_HEADERS, null);
        this.failOnUndocumentedParams = failOnUndocumentedParams;
    }

    public RequestHeaderSnippet failOnUndocumentedParams(boolean failOnUndocumentedParams) {
        return new RequestHeaderSnippet(failOnUndocumentedParams);
    }

    @Override
    protected boolean isRequired(MethodParameter param, RequestHeader annot) {
        // Spring disallows null for primitive types
        return param.getParameterType().isPrimitive() || annot.required();
    }

    @Override
    protected String getPath(RequestHeader annot) {
        return annot.value();
    }

    protected RequestHeader getAnnotation(MethodParameter param) {
        return param.getParameterAnnotation(RequestHeader.class);
    }

    @Override
    public String getHeaderKey() {
        return "request-headers";
    }

    @Override
    protected boolean shouldFailOnUndocumentedParams() {
        return failOnUndocumentedParams;
    }

    @Override
    protected String getDefaultValue(final RequestHeader annotation) {
        return annotation.defaultValue();
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[]{
                "th-header",
                "th-type",
                "th-optional",
                "th-description",
                "no-headers"
        };
    }
}
