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

package capital.scalable.restdocs.javadoc;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.hasText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

public class JavadocReaderImpl implements JavadocReader {
    private static final Logger log = getLogger(JavadocReader.class);

    private final Map<String, ClassJavadoc> classCache = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File javadocJsonDir;

    public JavadocReaderImpl() {
        this(null);
    }

    public JavadocReaderImpl(String javadocJsonDir) {
        if (javadocJsonDir != null) {
            this.javadocJsonDir = new File(javadocJsonDir).getAbsoluteFile();
        } else {
            this.javadocJsonDir = systemPropertyJavadocJsonDir();
        }

        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    private ClassJavadoc getClass(Class<?> clazz) {
        String packageName = clazz.getPackage().getName();
        String packageDir = packageName.replace(".", File.separator);
        String className = clazz.getCanonicalName().replaceAll(packageName + "\\.?", "");
        String fileName = packageDir + "/" + className + ".json";

        ClassJavadoc classJavadoc = classCache.get(fileName);
        if (classJavadoc != null) {
            return classJavadoc;
        }

        try {
            File docSource = makeRelativeToConfiguredJavaDocJsonDir(new File(fileName));
            classJavadoc = mapper
                    .readerFor(ClassJavadoc.class)
                    .readValue(docSource);
        } catch (FileNotFoundException e) {
            log.warn("No JavaDoc found for {} at {}", clazz.getCanonicalName(), fileName);
            classJavadoc = new ClassJavadoc();
        } catch (IOException e) {
            log.error("Problem reading file {}", fileName, e);
            classJavadoc = new ClassJavadoc();
        }

        classCache.put(fileName, classJavadoc);
        return classJavadoc;
    }

    @Override
    public String resolveFieldComment(Class<?> javaBaseClass, String javaFieldName) {
        return getClass(javaBaseClass).getFieldComment(javaFieldName);
    }

    @Override
    public String resolveMethodComment(Class<?> javaBaseClass, String javaMethodName) {
        return getClass(javaBaseClass).getMethodComment(javaMethodName);
    }

    @Override
    public String resolveMethodParameterComment(Class<?> javaBaseClass, String javaMethodName,
            String javaParameterName) {
        return getClass(javaBaseClass).getMethodParameterComment(javaMethodName, javaParameterName);
    }

    private File makeRelativeToConfiguredJavaDocJsonDir(File outputFile) {
        if (javadocJsonDir != null) {
            return new File(javadocJsonDir, outputFile.getPath());
        }
        return new File(outputFile.getPath());
    }

    private File systemPropertyJavadocJsonDir() {
        String outputDir = System.getProperties().getProperty(
                "org.springframework.restdocs.javadocJsonDir");
        if (hasText(outputDir)) {
            return new File(outputDir).getAbsoluteFile();
        }
        return null;
    }
}
