package capital.scalable.restdocs.jackson.javadoc;

import java.util.Map;

public class MethodJavadoc {
    private String comment;
    private Map<String, String> fields;

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

}
