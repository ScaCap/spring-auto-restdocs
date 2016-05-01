package capital.scalable.restdocs.jackson.javadoc;

import java.util.Map;

public class ClassJavadoc {
    private String comment;
    private Map<String, String> fields;
    private Map<String, MethodJavadoc> methods;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, MethodJavadoc> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, MethodJavadoc> methods) {
        this.methods = methods;
    }
}
