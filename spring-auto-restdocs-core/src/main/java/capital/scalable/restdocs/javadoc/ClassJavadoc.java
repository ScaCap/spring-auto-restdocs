package capital.scalable.restdocs.javadoc;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.HashMap;
import java.util.Map;

class ClassJavadoc {
    private String comment;
    private Map<String, FieldJavadoc> fields = new HashMap<>();
    private Map<String, MethodJavadoc> methods = new HashMap<>();

    public String getClassComment() {
        return comment;
    }

    public String getFieldComment(String fieldName) {
        FieldJavadoc fieldJavadoc = fields.get(fieldName);
        if (fieldJavadoc != null) {
            return trimToEmpty(fieldJavadoc.getComment());
        } else {
            return "";
        }
    }

    public String getMethodComment(String methodName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return trimToEmpty(methodJavadoc.getComment());
        } else {
            return "";
        }
    }

    public String getMethodParameterComment(String methodName, String parameterName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return trimToEmpty(methodJavadoc.getParameterComment(parameterName));
        } else {
            return "";
        }
    }

    public String getMethodTag(String javaMethodName, String tagName) {
        MethodJavadoc methodJavadoc = methods.get(javaMethodName);
        if (methodJavadoc != null) {
            return trimToEmpty(methodJavadoc.getTag(tagName));
        } else {
            return "";
        }
    }

    public String getFieldTag(String javaFieldName, String tagName) {
        FieldJavadoc fieldJavadoc = fields.get(javaFieldName);
        if (fieldJavadoc != null) {
            return trimToEmpty(fieldJavadoc.getTag(tagName));
        } else {
            return "";
        }
    }

    static class MethodJavadoc {
        private String comment;
        private Map<String, String> parameters = new HashMap<>();
        private Map<String, String> tags = new HashMap<>();

        public String getComment() {
            return comment;
        }

        public String getParameterComment(String parameterName) {
            return parameters.get(parameterName);
        }

        public String getTag(String tagName) {
            return tags.get(tagName);
        }
    }

    static class FieldJavadoc {
        private String comment;
        private Map<String, String> tags = new HashMap<>();

        public String getComment() {
            return comment;
        }

        public String getTag(String tagName) {
            return tags.get(tagName);
        }
    }
}
