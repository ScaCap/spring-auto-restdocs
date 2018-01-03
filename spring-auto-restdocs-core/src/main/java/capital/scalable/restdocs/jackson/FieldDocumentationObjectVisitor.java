package capital.scalable.restdocs.jackson;

import static capital.scalable.restdocs.util.TypeUtil.resolveAllTypes;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.springframework.util.Assert;

class FieldDocumentationObjectVisitor extends JsonObjectFormatVisitor.Base {
    private static final Logger log = getLogger(FieldDocumentationObjectVisitor.class);

    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final TypeRegistry typeRegistry;
    private final TypeFactory typeFactory;

    public FieldDocumentationObjectVisitor(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, TypeRegistry typeRegistry,
            TypeFactory typeFactory) {
        super(provider);
        this.context = context;
        this.path = path;
        this.typeRegistry = typeRegistry;
        this.typeFactory = typeFactory;
    }

    @Override
    public void optionalProperty(BeanProperty prop) throws JsonMappingException {
        String jsonName = prop.getName();
        String fieldName = prop.getMember().getName();

        JavaType baseType = prop.getType();
        Assert.notNull(baseType,
                "Missing type for property '" + jsonName + "', field '" + fieldName + "'");

        for (JavaType javaType : resolveAllTypes(baseType, typeFactory)) {
            JsonSerializer<?> ser = getProvider().findValueSerializer(javaType, prop);
            if (ser == null) {
                return;
            }

            visitType(prop, jsonName, fieldName, javaType, ser);
        }
    }

    private void visitType(BeanProperty prop, String jsonName, String fieldName, JavaType fieldType,
            JsonSerializer<?> ser) throws JsonMappingException {
        String fieldPath = path + (path.isEmpty() ? "" : ".") + jsonName;
        log.debug("({}) {}", fieldPath, fieldType.getRawClass().getSimpleName());
        Class<?> javaBaseClass = prop.getMember().getDeclaringClass();
        boolean shouldExpand = shouldExpand(prop);

        InternalFieldInfo fieldInfo = new InternalFieldInfo(javaBaseClass, fieldName, fieldType,
                fieldPath, shouldExpand);

        JsonFormatVisitorWrapper visitor = new FieldDocumentationVisitorWrapper(getProvider(),
                context, fieldPath, fieldInfo, typeRegistry, typeFactory);

        ser.acceptJsonFormatVisitor(visitor, fieldType);
    }

    private boolean shouldExpand(BeanProperty prop) {
        return prop.getMember().getAnnotation(RestdocsNotExpanded.class) == null;
    }
}
