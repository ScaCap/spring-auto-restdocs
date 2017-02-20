/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package capital.scalable.restdocs.jsondoclet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

/**
 * Javadoc to JSON doclet.
 */
public class ExtractDocumentationAsJsonDoclet extends Standard {

    public static boolean start(RootDoc root) {
        String destinationDir = destinationDir(root.options());
        for (ClassDoc classDoc : root.classes()) {
            ClassDocumentation cd = ClassDocumentation.fromClassDoc(classDoc);
            try {
                writeToFile(destinationDir, cd, classDoc);
            } catch (IOException e) {
                e.printStackTrace();
                throw new DocletAbortException("Error writing file: " + e);
            }
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
        return null;
    }

    private static void writeToFile(String destinationDir, ClassDocumentation cd, ClassDoc classDoc)
            throws IOException {
        final Path path = path(destinationDir, classDoc);
        Files.createDirectories(path);
        String fileName = classDoc.name() + ".json";
        cd.writeToFile(path.resolve(fileName));
    }

    private static Path path(String destinationDir, ClassDoc classDoc) {
        String packageName = classDoc.containingPackage().name();
        String packageDir = packageName.replace(".", File.separator);
        Path packagePath = Paths.get(packageDir);
        final Path path;
        if (destinationDir != null) {
            path = Paths.get(destinationDir).resolve(packageDir);
        } else {
            path = packagePath;
        }
        return path;
    }
}