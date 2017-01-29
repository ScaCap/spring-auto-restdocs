package capital.scalable.restdocs.jsondoclet;

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;

public class MethodDocumentation {
    private String comment;
    private Map<String, String> parameters = new HashMap<>();

    private MethodDocumentation() {
        // enforce usage of static factory method
    }

    public static MethodDocumentation fromMethodDoc(MethodDoc methodDoc) {
        MethodDocumentation md = new MethodDocumentation();
        md.comment = JsonUtils.escape(methodDoc.commentText());

        for (ParamTag param : methodDoc.paramTags()) {
            md.parameters.put(param.parameterName(), JsonUtils.escape(param.parameterComment()));
        }
        return md;
    }

    public String toJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"comment\":\"");
        builder.append(comment);
        builder.append("\", \"parameters\":{");
        for (Map.Entry<String, String> e : parameters.entrySet()) {
            builder.append("\"");
            builder.append(e.getKey());
            builder.append("\":\"");
            builder.append(e.getValue());
            builder.append("\",");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}}");
        return builder.toString();
    }
}
