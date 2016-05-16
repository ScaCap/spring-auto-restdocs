package capital.scalable.restdocs.jackson.javadoc;

import static org.springframework.util.StringUtils.hasText;

import java.util.HashMap;
import java.util.Map;

class ClassJavadoc {
    private String comment;
    private Map<String, String> fields = new HashMap<>();
    private Map<String, MethodJavadoc> methods = new HashMap<>();

    public String getClassComment() {
        return comment;
    }

    public String getFieldComment(String fieldName) {
        return trimToEmpty(fields.get(fieldName));
    }

    public String getMethodComment(String methodName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return methodJavadoc.getComment();
        } else {
            return "";
        }
    }

    public String getMethodParameterComment(String methodName, String parameterName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return methodJavadoc.getParameterComment(parameterName);
        } else {
            return "";
        }
    }

    private static String trimToEmpty(String value) {
        return hasText(value) ? value : "";
    }

    static class MethodJavadoc {
        private String comment;
        private Map<String, String> parameters = new HashMap<>();

        public String getComment() {
            return trimToEmpty(comment);
        }

        public String getParameterComment(String parameterName) {
            return trimToEmpty(parameters.get(parameterName));
        }
    }
}
