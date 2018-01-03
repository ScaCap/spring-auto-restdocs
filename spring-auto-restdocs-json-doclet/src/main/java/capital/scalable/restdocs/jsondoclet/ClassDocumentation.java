package capital.scalable.restdocs.jsondoclet;

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

public final class ClassDocumentation {
    private String comment = "";
    private final Map<String, FieldDocumentation> fields = new HashMap<>();
    private final Map<String, MethodDocumentation> methods = new HashMap<>();

    private ClassDocumentation() {
        // enforce usage of static factory method
    }

    public static ClassDocumentation fromClassDoc(ClassDoc classDoc) {
        ClassDocumentation cd = new ClassDocumentation();
        cd.setComment(classDoc.commentText());
        for (FieldDoc fieldDoc : classDoc.fields(false)) {
            cd.addField(fieldDoc);
        }
        for (MethodDoc methodDoc : classDoc.methods(false)) {
            cd.addMethod(methodDoc);
        }
        return cd;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    private void addField(FieldDoc fieldDoc) {
        this.fields.put(fieldDoc.name(), FieldDocumentation.fromFieldDoc(fieldDoc));
    }

    private void addMethod(MethodDoc methodDoc) {
        this.methods.put(methodDoc.name(), MethodDocumentation.fromMethodDoc(methodDoc));
    }
}
