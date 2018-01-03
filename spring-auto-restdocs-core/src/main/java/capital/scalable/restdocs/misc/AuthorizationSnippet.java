package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.getAuthorization;
import static capital.scalable.restdocs.OperationAttributeHelper.setAuthorization;

import java.util.HashMap;
import java.util.Map;

import capital.scalable.restdocs.section.SectionSupport;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class AuthorizationSnippet extends TemplatedSnippet implements SectionSupport {

    public static final String AUTHORIZATION = "auto-authorization";

    private final String defaultAuthorization;

    public AuthorizationSnippet(String defaultAuthorization) {
        super(AUTHORIZATION, null);
        this.defaultAuthorization = defaultAuthorization;
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
        model.put("authorization", authorizationDescription(operation));
        return model;
    }

    private String authorizationDescription(Operation operation) {
        String requestAuthorization = getAuthorization(operation);
        if (requestAuthorization != null) {
            return requestAuthorization;
        } else {
            return defaultAuthorization;
        }
    }

    public static MockHttpServletRequest documentAuthorization(MockHttpServletRequest request,
            String authorization) {
        setAuthorization(request, authorization);
        return request;
    }

    @Override
    public String getFileName() {
        return getSnippetName();
    }

    @Override
    public String getHeaderKey() {
        return "authorization";
    }

    @Override
    public boolean hasContent(Operation operation) {
        return true; // if this snippet is included, always print at least default value
    }
}
