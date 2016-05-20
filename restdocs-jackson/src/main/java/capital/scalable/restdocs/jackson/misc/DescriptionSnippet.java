package capital.scalable.restdocs.jackson.misc;

import static capital.scalable.restdocs.jackson.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.jackson.OperationAttributeHelper.getJavadocReader;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

public class DescriptionSnippet extends TemplatedSnippet {

    public DescriptionSnippet() {
        super("description", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        HandlerMethod handlerMethod = getHandlerMethod(operation);

        String methodComment = getJavadocReader(operation)
                .resolveMethodComment(handlerMethod.getBeanType(),
                        handlerMethod.getMethod().getName());

        Map<String, Object> model = new HashMap<>();
        model.put("description", methodComment);
        return model;
    }
}