/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.StandardDoclet;

/**
 * Javadoc to JSON doclet.
 */
public class ExtractDocumentationAsJsonDoclet extends StandardDoclet {

    private String directoryLocationPath;


    @Override
    public boolean run(DocletEnvironment docEnv) {
        Path destinationDir = getDestinationDir();
        ObjectMapper mapper = createObjectMapper();

        docEnv.getIncludedElements()
                .stream().parallel()
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
        if (directoryLocationPath != null) {
            return Paths.get(directoryLocationPath).toAbsolutePath();
        } else {
            return Paths.get("../generated-javadoc-json").toAbsolutePath();
        }
    }

    @Override
    public Set<Doclet.Option> getSupportedOptions() {
        Set<Doclet.Option> result = new HashSet<>();

        Doclet.Option directoryLocationOption = new DirectoryLocationOption() {
            @Override
            public boolean process(String opt, List<String> args) {
                directoryLocationPath = args.get(0);
                return true;
            }
        };
        result.add(directoryLocationOption);
        result.addAll(super.getSupportedOptions());
        return result;
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
}
