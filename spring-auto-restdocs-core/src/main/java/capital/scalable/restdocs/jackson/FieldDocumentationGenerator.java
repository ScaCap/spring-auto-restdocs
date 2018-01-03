package capital.scalable.restdocs.jackson;

import static capital.scalable.restdocs.util.TypeUtil.resolveAllTypes;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Type;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.springframework.restdocs.payload.FieldDescriptor;

public class FieldDocumentationGenerator {
    private static final Logger log = getLogger(FieldDocumentationGenerator.class);

    private final ObjectWriter writer;
    private final DeserializationConfig deserializationConfig;
    private final JavadocReader javadocReader;
    private final ConstraintReader constraintReader;

    public FieldDocumentationGenerator(ObjectWriter writer,
            DeserializationConfig deserializationConfig,
            JavadocReader javadocReader,
            ConstraintReader constraintReader) {
        this.writer = writer;
        this.deserializationConfig = deserializationConfig;
        this.javadocReader = javadocReader;
        this.constraintReader = constraintReader;
    }

    public List<FieldDescriptor> generateDocumentation(Type baseType, TypeFactory typeFactory)
            throws JsonMappingException {
        JavaType javaBaseType = typeFactory.constructType(baseType);
        List<JavaType> types = resolveAllTypes(javaBaseType, typeFactory);

        FieldDocumentationVisitorWrapper visitorWrapper = FieldDocumentationVisitorWrapper.create(
                javadocReader, constraintReader, deserializationConfig,
                new TypeRegistry(types), typeFactory);

        for (JavaType type : types) {
            log.debug("(TOP) {}", type.getRawClass().getSimpleName());
            writer.acceptJsonFormatVisitor(type, visitorWrapper);
        }

        return visitorWrapper.getFields();
    }
}