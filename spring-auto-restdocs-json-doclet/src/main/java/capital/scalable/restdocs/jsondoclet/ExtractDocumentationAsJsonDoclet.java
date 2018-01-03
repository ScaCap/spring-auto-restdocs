package capital.scalable.restdocs.jsondoclet;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

/**
 * Javadoc to JSON doclet.
 */
public class ExtractDocumentationAsJsonDoclet extends Standard {

    public static boolean start(RootDoc root) {
        String destinationDir = destinationDir(root.options());
        ObjectMapper mapper = createObjectMapper();

        for (ClassDoc classDoc : root.classes()) {
            ClassDocumentation cd = ClassDocumentation.fromClassDoc(classDoc);
            writeToFile(destinationDir, mapper, classDoc, cd);
        }
        return true;
    }

    private static String destinationDir(String[][] options) {
        for (String[] os : options) {
            String opt = os[0].toLowerCase();
            if (opt.equals("-d")) {
                return os[1];
            }
        }
        return "../generated-javadoc-json";
    }

    private static void writeToFile(String destinationDir, ObjectMapper mapper,
            ClassDoc classDoc, ClassDocumentation cd) {
        try {
            Path path = path(destinationDir, classDoc);
            try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
                mapper.writerFor(ClassDocumentation.class).writeValue(writer, cd);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new DocletAbortException("Error writing file: " + e);
        }
    }

    private static Path path(String destinationDir, ClassDoc classDoc) throws IOException {
        String packageName = classDoc.containingPackage().name();
        String packageDir = packageName.replace(".", File.separator);
        Path packagePath = Paths.get(packageDir);

        final Path path;
        if (destinationDir != null) {
            path = Paths.get(destinationDir).resolve(packageDir);
        } else {
            path = packagePath;
        }

        Files.createDirectories(path);

        return path.resolve(classDoc.name() + ".json");
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return mapper;
    }
}