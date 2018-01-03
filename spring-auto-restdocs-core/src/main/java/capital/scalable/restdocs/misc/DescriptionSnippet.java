package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.determineTemplateFormatting;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.javadoc.JavadocUtil.convertFromJavadoc;
import static capital.scalable.restdocs.util.FormatUtil.addDot;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.Map;

import capital.scalable.restdocs.javadoc.JavadocReader;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

public class DescriptionSnippet extends TemplatedSnippet {

    public static final String DESCRIPTION = "auto-description";

    public DescriptionSnippet() {
        super(DESCRIPTION, null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        HandlerMethod handlerMethod = getHandlerMethod(operation);
        Map<String, Object> model = defaultModel();
        if (handlerMethod == null) {
            return model;
        }

        JavadocReader javadocReader = getJavadocReader(operation);
        String methodComment = resolveComment(handlerMethod, javadocReader);
        String deprecated = resolveDeprecated(handlerMethod, javadocReader);
        String description = convertFromJavadoc(deprecated + methodComment,
                determineTemplateFormatting(operation));

        model.put("description", description);
        return model;
    }

    private String resolveDeprecated(HandlerMethod handlerMethod, JavadocReader javadocReader) {
        boolean isDeprecated = handlerMethod.getMethod().getAnnotation(Deprecated.class) != null;
        String comment = javadocReader.resolveMethodTag(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName(), "deprecated");
        if (isDeprecated || isNotBlank(comment)) {
            comment = capitalize(addDot(comment));
            return "<b>Deprecated.</b> " + comment + "<p>";
        } else {
            return "";
        }
    }

    private String resolveComment(HandlerMethod handlerMethod, JavadocReader javadocReader) {
        String methodComment = javadocReader.resolveMethodComment(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName());
        return capitalize(addDot(methodComment));
    }

    private Map<String, Object> defaultModel() {
        Map<String, Object> model = new HashMap<>();
        model.put("description", "");
        return model;
    }
}
