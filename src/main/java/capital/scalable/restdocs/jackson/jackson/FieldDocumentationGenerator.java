package capital.scalable.restdocs.jackson.jackson;

import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.restdocs.payload.FieldDescriptor;

public class FieldDocumentationGenerator {

    private final ObjectWriter writer;

    public FieldDocumentationGenerator(ObjectWriter writer) {
        this.writer = writer;
    }

    public List<FieldDescriptor> generateDocumentation(Type type, TypeFactory typeFactory)
            throws JsonMappingException {
        return generateDocumentation(typeFactory.constructType(type));
    }

    public List<FieldDescriptor> generateDocumentation(JavaType type) throws JsonMappingException {
        FieldDocumentationVisitorWrapper visitorWrapper = FieldDocumentationVisitorWrapper.create();
        writer.acceptJsonFormatVisitor(type, visitorWrapper);
        return visitorWrapper.getContext().getFields();
    }
}
