package capital.scalable.restdocs.payload;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class JacksonRequestFieldSnippet extends AbstractJacksonFieldSnippet {

    public static final String REQUEST_FIELDS = "auto-request-fields";

    private final Type requestBodyType;
    private final boolean failOnUndocumentedFields;

    public JacksonRequestFieldSnippet() {
        this(null, false);
    }

    public JacksonRequestFieldSnippet(Type requestBodyType, boolean failOnUndocumentedFields) {
        super(REQUEST_FIELDS, null);
        this.requestBodyType = requestBodyType;
        this.failOnUndocumentedFields = failOnUndocumentedFields;
    }

    public JacksonRequestFieldSnippet requestBodyAsType(Type requestBodyType) {
        return new JacksonRequestFieldSnippet(requestBodyType, failOnUndocumentedFields);
    }

    public JacksonRequestFieldSnippet failOnUndocumentedFields(boolean failOnUndocumentedFields) {
        return new JacksonRequestFieldSnippet(requestBodyType, failOnUndocumentedFields);
    }

    @Override
    protected Type getType(HandlerMethod method) {
        if (requestBodyType != null) {
            return requestBodyType;
        }

        for (MethodParameter param : method.getMethodParameters()) {
            if (isRequestBody(param) || isModelAttribute(param)) {
                return getType(param);
            }
        }
        return null;
    }

    private boolean isRequestBody(MethodParameter param) {
        return param.getParameterAnnotation(RequestBody.class) != null;
    }

    private boolean isModelAttribute(MethodParameter param) {
        return param.getParameterAnnotation(ModelAttribute.class) != null;
    }

    private Type getType(final MethodParameter param) {
        if (isCollection(param.getParameterType())) {
            return new GenericArrayType() {

                @Override
                public Type getGenericComponentType() {
                    return firstGenericType(param);
                }
            };
        } else {
            return param.getParameterType();
        }
    }

    @Override
    public String getHeaderKey() {
        return "request-fields";
    }

    @Override
    protected boolean shouldFailOnUndocumentedFields() {
        return failOnUndocumentedFields;
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[]{
                "th-path",
                "th-type",
                "th-optional",
                "th-description",
                "no-request-body"
        };
    }
}
