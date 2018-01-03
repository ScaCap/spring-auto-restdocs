package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

class FieldDocumentationArrayVisitor extends JsonArrayFormatVisitor.Base {

    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final TypeRegistry typeRegistry;
    private final TypeFactory typeFactory;

    public FieldDocumentationArrayVisitor(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, TypeRegistry typeRegistry,
            TypeFactory typeFactory) {
        super(provider);
        this.context = context;
        this.path = path;
        this.typeRegistry = typeRegistry;
        this.typeFactory = typeFactory;
    }

    @Override
    public void itemsFormat(JsonFormatVisitable handler, JavaType elementType)
            throws JsonMappingException {
        String elementPath = path + "[]";
        JsonFormatVisitorWrapper visitor = new FieldDocumentationVisitorWrapper(getProvider(),
                context, elementPath, null, typeRegistry, typeFactory);
        handler.acceptJsonFormatVisitor(visitor, elementType);
    }
}
