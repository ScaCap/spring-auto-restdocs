package capital.scalable.restdocs.jackson.misc;

import static capital.scalable.restdocs.jackson.OperationAttributeHelper.getAuthorization;
import static capital.scalable.restdocs.jackson.OperationAttributeHelper.setAuthorization;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class AuthorizationSnippet extends TemplatedSnippet {

    private final String defaultAuthorization;

    public AuthorizationSnippet(String defaultAuthorization) {
        super("authorization", null);
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
}