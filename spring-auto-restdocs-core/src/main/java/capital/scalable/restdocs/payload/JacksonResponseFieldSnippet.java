package capital.scalable.restdocs.payload;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;

public class JacksonResponseFieldSnippet extends AbstractJacksonFieldSnippet {

    public static final String RESPONSE_FIELDS = "auto-response-fields";
    public static final String SPRING_DATA_PAGE_CLASS = "org.springframework.data.domain.Page";

    private final Type responseBodyType;
    private final boolean failOnUndocumentedFields;

    public JacksonResponseFieldSnippet() {
        this(null, false);
    }

    public JacksonResponseFieldSnippet(Type responseBodyType, boolean failOnUndocumentedFields) {
        super(RESPONSE_FIELDS, null);
        this.responseBodyType = responseBodyType;
        this.failOnUndocumentedFields = failOnUndocumentedFields;
    }

    public JacksonResponseFieldSnippet responseBodyAsType(Type responseBodyType) {
        return new JacksonResponseFieldSnippet(responseBodyType, failOnUndocumentedFields);
    }

    public JacksonResponseFieldSnippet failOnUndocumentedFields(boolean failOnUndocumentedFields) {
        return new JacksonResponseFieldSnippet(responseBodyType, failOnUndocumentedFields);
    }

    @Override
    protected Type getType(final HandlerMethod method) {
        if (responseBodyType != null) {
            return responseBodyType;
        }

        Class<?> returnType = method.getReturnType().getParameterType();
        if (returnType == ResponseEntity.class) {
            return firstGenericType(method.getReturnType());
        } else if (returnType == HttpEntity.class) {
            return firstGenericType(method.getReturnType());
        } else if (SPRING_DATA_PAGE_CLASS.equals(returnType.getCanonicalName())) {
            return firstGenericType(method.getReturnType());
        } else if (isCollection(returnType)) {
            return new GenericArrayType() {

                @Override
                public Type getGenericComponentType() {
                    return firstGenericType(method.getReturnType());
                }
            };
        } else if ("void".equals(returnType.getName())) {
            return null;
        } else {
            return returnType;
        }
    }

    @Override
    protected void enrichModel(Map<String, Object> model, HandlerMethod handlerMethod) {
        model.put("isPageResponse", isPageResponse(handlerMethod));
    }

    private boolean isPageResponse(HandlerMethod handlerMethod) {
        return SPRING_DATA_PAGE_CLASS.equals(
                handlerMethod.getReturnType().getParameterType().getCanonicalName());
    }

    @Override
    public String getHeaderKey() {
        return "response-fields";
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
                "pagination-response-adoc",
                "pagination-response-md",
                "no-response-body"
        };
    }
}
