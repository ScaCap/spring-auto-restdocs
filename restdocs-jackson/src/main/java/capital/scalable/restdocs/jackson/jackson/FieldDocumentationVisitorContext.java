package capital.scalable.restdocs.jackson.jackson;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.util.StringUtils;

public class FieldDocumentationVisitorContext {

    private static final Logger log = LoggerFactory.getLogger(FieldDocumentationVisitorContext.class);

    private final ObjectMapper internalObjectMapper = new ObjectMapper();

    private final List<FieldDescriptor> fields = new ArrayList<>();

    public List<FieldDescriptor> getFields() {
        return fields;
    }

    public void addField(InternalFieldInfo info, Object jsonFieldType) {
        JavaDocOfClass javaDoc = loadJavaDoc(info.getJavaBaseClass());
        String comment = null;
        if (javaDoc != null) {
            comment = javaDoc.getFields().get(info.getJavaFieldName());
        }
        if (comment == null) {
            comment = "";
        }
        FieldDescriptor fieldDescriptor = fieldWithPath(info.getJsonFieldPath())
                .type(jsonFieldType)
                .description(comment);
        if (info.getOptional()) {
            fieldDescriptor.optional();
        }
        fields.add(fieldDescriptor);
    }

    private JavaDocOfClass loadJavaDoc(Class<?> clazz) {
        String fileName = clazz.getCanonicalName() + ".json";
        try {
            return internalObjectMapper.readerFor(JavaDocOfClass.class).readValue(
                    makeRelativeToConfiguredJavaDocJsonDir(new File(fileName)));
        } catch (IOException e) {
            log.info("No JavaDoc found for {}", clazz.getCanonicalName());
            return null;
        }
    }

    private File makeRelativeToConfiguredJavaDocJsonDir(File outputFile) {
        File configuredJavaDocJsonDir = getJavaDocJsonDir();
        if (configuredJavaDocJsonDir != null) {
            return new File(configuredJavaDocJsonDir, outputFile.getPath());
        }
        return new File(outputFile.getPath());
    }

    private File getJavaDocJsonDir() {
        String outputDir = System.getProperties().getProperty(
                "org.springframework.restdocs.javaDocJsonDir");
        if (StringUtils.hasText(outputDir)) {
            return new File(outputDir).getAbsoluteFile();
        }
        return null;
    }

    private static class JavaDocOfClass {
        private String comment;
        private Map<String, String> fields;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Map<String, String> getFields() {
            return fields;
        }

        public void setFields(Map<String, String> fields) {
            this.fields = fields;
        }
    }
}
