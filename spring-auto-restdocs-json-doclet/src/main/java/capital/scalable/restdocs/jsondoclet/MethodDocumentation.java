package capital.scalable.restdocs.jsondoclet;

import static capital.scalable.restdocs.jsondoclet.DocletUtils.cleanupTagName;

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;

public class MethodDocumentation {
    private String comment = "";
    private final Map<String, String> parameters = new HashMap<>();
    private final Map<String, String> tags = new HashMap<>();

    private MethodDocumentation() {
        // enforce usage of static factory method
    }

    public static MethodDocumentation fromMethodDoc(MethodDoc methodDoc) {
        MethodDocumentation md = new MethodDocumentation();
        md.comment = methodDoc.commentText();

        for (Tag tag : methodDoc.tags()) {
            if (tag instanceof ParamTag) {
                ParamTag paramTag = (ParamTag) tag;
                md.parameters.put(paramTag.parameterName(), paramTag.parameterComment());
            } else {
                md.tags.put(cleanupTagName(tag.name()), tag.text());
            }
        }

        return md;
    }
}
