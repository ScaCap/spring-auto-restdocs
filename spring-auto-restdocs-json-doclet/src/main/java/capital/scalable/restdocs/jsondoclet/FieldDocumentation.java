package capital.scalable.restdocs.jsondoclet;

import static capital.scalable.restdocs.jsondoclet.DocletUtils.cleanupTagName;

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Tag;

public class FieldDocumentation {

    private String comment;
    private final Map<String, String> tags = new HashMap<>();

    public static FieldDocumentation fromFieldDoc(FieldDoc fieldDoc) {
        FieldDocumentation fd = new FieldDocumentation();
        fd.comment = fieldDoc.commentText();

        for (Tag tag : fieldDoc.tags()) {
            fd.tags.put(cleanupTagName(tag.name()), tag.text());
        }

        return fd;
    }

}
