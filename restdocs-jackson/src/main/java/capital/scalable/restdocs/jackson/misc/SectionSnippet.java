package capital.scalable.restdocs.jackson.misc;

import static capital.scalable.restdocs.jackson.OperationAttributeHelper.getHandlerMethod;
import static org.springframework.util.StringUtils.capitalize;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippet extends TemplatedSnippet {

    public SectionSnippet() {
        super("section", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        HandlerMethod handlerMethod = getHandlerMethod(operation);
        String title = splitCamelCase(capitalize(handlerMethod.getMethod().getName()));

        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("link", delimit(operation.getName()));
        model.put("path", operation.getName());
        return model;
    }

    private String delimit(String value) {
        return value.replace("/", "-");
    }

    // http://stackoverflow.com/a/2560017/410531
    static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }
}