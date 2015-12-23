package capital.scalable.restdocs.jackson.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class FieldDocumentationArrayVisitor extends JsonArrayFormatVisitor.Base {

    private final FieldDocumentationVisitorContext context;
    private final String path;

    public FieldDocumentationArrayVisitor(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path) {
        super(provider);
        this.context = context;
        this.path = path;
    }

    @Override
    public void itemsFormat(JsonFormatVisitable handler, JavaType elementType)
            throws JsonMappingException {
        String elementPath = path + "[]";
        JsonFormatVisitorWrapper visitor =
                new FieldDocumentationVisitorWrapper(getProvider(), context, elementPath, null);
        handler.acceptJsonFormatVisitor(visitor, elementType);
    }
}
