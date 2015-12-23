package capital.scalable.restdocs.jackson.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import org.springframework.restdocs.payload.JsonFieldType;

public class FieldDocumentationVisitorWrapper implements JsonFormatVisitorWrapper {
    private SerializerProvider provider;
    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final InternalFieldInfo fieldInfo;

    FieldDocumentationVisitorWrapper(FieldDocumentationVisitorContext context, String path,
            InternalFieldInfo fieldInfo) {
        this(null, context, path, fieldInfo);
    }

    FieldDocumentationVisitorWrapper(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, InternalFieldInfo fieldInfo) {
        this.provider = provider;
        this.context = context;
        this.path = path;
        this.fieldInfo = fieldInfo;
    }

    public static FieldDocumentationVisitorWrapper create() {
        return new FieldDocumentationVisitorWrapper(new FieldDocumentationVisitorContext(), "",
                null);
    }

    @Override
    public SerializerProvider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(SerializerProvider provider) {
        this.provider = provider;
    }

    @Override
    public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.OBJECT);
        return new FieldDocumentationObjectVisitor(provider, type.getRawClass(), context, path);
    }

    @Override
    public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.ARRAY);
        return new FieldDocumentationArrayVisitor(provider, context, path);
    }

    @Override
    public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.STRING);
        return new JsonStringFormatVisitor.Base();
    }

    @Override
    public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.NUMBER);
        return new JsonNumberFormatVisitor.Base();
    }

    @Override
    public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.NUMBER);
        return new JsonIntegerFormatVisitor.Base();
    }

    @Override
    public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.BOOLEAN);
        return new JsonBooleanFormatVisitor.Base();
    }

    @Override
    public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.NULL);
        return new JsonNullFormatVisitor.Base();
    }

    @Override
    public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.VARIES);
        return new JsonAnyFormatVisitor.Base();
    }

    @Override
    public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.OBJECT);
        return new JsonMapFormatVisitor.Base(provider);
    }

    public FieldDocumentationVisitorContext getContext() {
        return context;
    }

    private void addFieldIfPresent(JsonFieldType fieldType) {
        if (fieldInfo != null) {
            context.addField(fieldInfo, fieldType);
        }
    }
}
