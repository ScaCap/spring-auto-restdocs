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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

public class JavadocReaderImpl implements JavadocReader {
    private static final Logger log = getLogger(JavadocReader.class);
    private static final String PATH_DELIMITER = ",";
    private static final String JAVADOC_JSON_DIR_PROPERTY =
            "org.springframework.restdocs.javadocJsonDir";

    private final Map<String, ClassJavadoc> classCache = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final List<File> absoluteBaseDirs;

    private JavadocReaderImpl(ObjectMapper mapper, List<File> absoluteBaseDirs) {
        this.mapper = mapper;
        this.absoluteBaseDirs = absoluteBaseDirs;
    }

    public static JavadocReaderImpl createWithSystemProperty() {
        String systemProperty = System.getProperties().getProperty(JAVADOC_JSON_DIR_PROPERTY);
        return new JavadocReaderImpl(objectMapper(), toAbsoluteDirs(systemProperty));
    }

    /**
     * Used for testing.
     */
    static JavadocReaderImpl createWith(String javadocJsonDir) {
        return new JavadocReaderImpl(objectMapper(), toAbsoluteDirs(javadocJsonDir));
    }

    @Override
    public String resolveFieldComment(Class<?> javaBaseClass, String javaFieldName) {
        return classJavadoc(javaBaseClass).getFieldComment(javaFieldName);
    }

    @Override
    public String resolveMethodComment(Class<?> javaBaseClass, String javaMethodName) {
        return classJavadoc(javaBaseClass).getMethodComment(javaMethodName);
    }

    @Override
    public String resolveMethodParameterComment(Class<?> javaBaseClass, String javaMethodName,
            String javaParameterName) {
        return classJavadoc(javaBaseClass)
                .getMethodParameterComment(javaMethodName, javaParameterName);
    }

    private ClassJavadoc classJavadoc(Class<?> clazz) {
        String relativePath = classToRelativePath(clazz);
        ClassJavadoc classJavadocFromCache = classCache.get(relativePath);
        if (classJavadocFromCache != null) {
            return classJavadocFromCache;
        } else {
            ClassJavadoc classJavadoc = readFiles(clazz, relativePath);
            classCache.put(relativePath, classJavadoc);
            return classJavadoc;
        }
    }

    private String classToRelativePath(Class<?> clazz) {
        String packageName = clazz.getPackage().getName();
        String packageDir = packageName.replace(".", File.separator);
        String className = clazz.getCanonicalName().replaceAll(packageName + "\\.?", "");
        return new File(packageDir, className + ".json").getPath();
    }

    private ClassJavadoc readFiles(Class<?> clazz, String relativePath) {
        if (absoluteBaseDirs.isEmpty()) {
            // No absolute directory is configured and thus we try to find the file relative.
            ClassJavadoc classJavadoc = readFile(new File(relativePath));
            if (classJavadoc != null) {
                return classJavadoc;
            }
        } else {
            // Try to find the file in all configured directories.
            for (File dir : absoluteBaseDirs) {
                ClassJavadoc classJavadoc = readFile(new File(dir, relativePath));
                if (classJavadoc != null) {
                    return classJavadoc;
                }
            }
        }
        log.warn("No Javadoc found for class {}", clazz.getCanonicalName());
        return new ClassJavadoc();
    }

    private ClassJavadoc readFile(File docSource) {
        try {
            return mapper
                    .readerFor(ClassJavadoc.class)
                    .readValue(docSource);
        } catch (FileNotFoundException e) {
            // Ignored as we might try more than one file and we warn if no Javadoc file
            // is found at the end.
        } catch (IOException e) {
            log.error("Failed to read file {}", docSource.getName(), e);
        }
        return null;
    }

    private static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return mapper;
    }

    private static List<File> toAbsoluteDirs(String javadocJsonDirs) {
        List<File> absoluteDirs = new ArrayList<>();
        if (isNotBlank(javadocJsonDirs)) {
            String[] dirs = split(javadocJsonDirs, PATH_DELIMITER);
            for (String dir : dirs) {
                if (isNotBlank(dir)) {
                    absoluteDirs.add(new File(dir.trim()).getAbsoluteFile());
                }
            }
        }
        return absoluteDirs;
    }
}
