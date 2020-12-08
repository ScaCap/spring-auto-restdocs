/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK14+
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package capital.scalable.restdocs.jsondoclet;

import static java.nio.charset.StandardCharsets.UTF_8;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.StandardDoclet;

/**
 * Javadoc to JSON doclet.
 * <p>
 * Implementation note: this doclet extends the default doclet, mainly to be able to ignore the default parameters.
 * See the {@link ExtractDocumentationAsJsonDoclet#getSupportedOptions()} method.
 */
public class ExtractDocumentationAsJsonDoclet extends StandardDoclet {

    private String directoryLocationPath;

    @Override
    public boolean run(DocletEnvironment docEnv) {
        Path destinationDir = getDestinationDir();
        ObjectMapper mapper = createObjectMapper();

        docEnv.getIncludedElements()
                .stream()
                .filter(e -> e.getKind().isClass() || e.getKind().isInterface())
                .forEach(classOrInterface -> writeToFile(
                        destinationDir,
                        mapper,
                        findPackageElement(classOrInterface),
                        (TypeElement) classOrInterface,
                        ClassDocumentation.fromClassDoc(docEnv, classOrInterface)
                ));

        return true;
    }

    private Path getDestinationDir() {
        String path = Objects.requireNonNullElse(directoryLocationPath, "../generated-javadoc-json");
        return Paths.get(path).toAbsolutePath();
    }

    /**
     * Running a doclet with unknown options results in an error. Therefore, we include all default options in the
     * list of possible options, even though we have no way of accessing those. If we want one of the default options,
     * we need to extract them and override them.
     *
     * @return The options we want and those we want to ignore.
     */
    @Override
    public Set<Doclet.Option> getSupportedOptions() {

        Set<Doclet.Option> allOptions = new HashSet<>(super.getSupportedOptions());

        // Get the option for -d, which is the one we want
        Doclet.Option locationOption = allOptions.stream()
                .filter(o -> o.getNames().contains("-d"))
                .findFirst().orElseGet(FallbackDirectoryOption::new);

        // Construct a wrapper around it, to be able to get access the argument.
        Doclet.Option wrapped = new WrappingOption(locationOption) {
            @Override
            public boolean process(String option, List<String> arguments) {
                directoryLocationPath = arguments.get(0);
                return true;
            }
        };

        // Remove the old option and add the wrapper in it's place.
        allOptions.remove(locationOption);
        allOptions.add(wrapped);

        return allOptions;
    }

    private static PackageElement findPackageElement(Element classOrInterface) {
        Element pkg = classOrInterface.getEnclosingElement();
        int i = 10;
        while (!ElementKind.PACKAGE.equals(pkg.getKind())) {
            if (i-- > 0) {
                pkg = pkg.getEnclosingElement();
            } else {
                throw new DocletAbortException("Class or interface " + classOrInterface + "to deeply nested!");
            }
        }
        return (PackageElement) pkg;
    }

    private static void writeToFile(Path destinationDir, ObjectMapper mapper,
            PackageElement packageElement, TypeElement classOrInterface,
            ClassDocumentation cd) {
        try {
            Path path = path(destinationDir, packageElement, classOrInterface);
            try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
                mapper.writerFor(ClassDocumentation.class).writeValue(writer, cd);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new DocletAbortException("Error writing file: " + e);
        }
    }

    private static Path path(Path destinationDir, PackageElement packageElement,
            TypeElement classOrInterface) throws IOException {
        String packageName = packageElement.getQualifiedName().toString();
        String packageDir = packageName.replace(".", File.separator);
        Path packagePath = Paths.get(packageDir);

        final Path path;
        if (destinationDir != null) {
            path = destinationDir.resolve(packageDir);
        } else {
            path = packagePath;
        }

        Files.createDirectories(path);

        String filename = classOrInterface.getQualifiedName().toString()
                .replace(packageElement.getQualifiedName() + ".", "");
        return path.resolve(filename + ".json");
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

    /**
     * Supports launching Javadoc as an application that can be debugged in an IDE. 
     * @param args currently unused
     */
    public static void main(String... args) {
        String[] docletArgs = new String[] {
            "--enable-preview",
            "--release", "14",
            "-doclet", ExtractDocumentationAsJsonDoclet.class.getName(),
            "-docletpath", "target/classes/", 
            "-sourcepath", "src/test/samples/", 
            "foo"
        };
        DocumentationTool docTool = ToolProvider.getSystemDocumentationTool();
        docTool.run(System.in, System.out, System.err, docletArgs);
    }
}
