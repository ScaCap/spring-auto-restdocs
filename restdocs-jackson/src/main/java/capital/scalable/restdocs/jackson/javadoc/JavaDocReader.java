package capital.scalable.restdocs.jackson.javadoc;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

public class JavadocReader {
    private static final Logger log = getLogger(JavadocReader.class);

    private final ObjectMapper internalObjectMapper = new ObjectMapper();

    public ClassJavadoc loadClass(Class<?> clazz) {
        String fileName = clazz.getCanonicalName() + ".json";
        try {
            return internalObjectMapper.readerFor(ClassJavadoc.class).readValue(
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
}
