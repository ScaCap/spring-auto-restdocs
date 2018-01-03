package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.getRequestMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getRequestPattern;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class MethodAndPathSnippet extends TemplatedSnippet {

    public static final String METHOD_PATH = "auto-method-path";

    public MethodAndPathSnippet() {
        super(METHOD_PATH, null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
        model.put("method", getRequestMethod(operation));
        model.put("path", nullToEmpty(getRequestPattern(operation)));
        return model;
    }

    private String nullToEmpty(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}