/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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
package capital.scalable.restdocs.javadoc;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
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
        String jsonDir = System.getProperties().getProperty(JAVADOC_JSON_DIR_PROPERTY);
        if (StringUtils.isEmpty(jsonDir)) {
            jsonDir = getDefaultJsonDirectory();
        }
        return new JavadocReaderImpl(objectMapper(), toAbsoluteDirs(jsonDir));
    }

    private static String getDefaultJsonDirectory() {
        if (new File("pom.xml").exists()) {
            return "target/generated-javadoc-json";
        }
        return "build/generated-javadoc-json";
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
    public String resolveFieldTag(Class<?> javaBaseClass, String javaFieldName, String tagName) {
        return classJavadoc(javaBaseClass).getFieldTag(javaFieldName, tagName);
    }

    @Override
    public String resolveMethodComment(Class<?> javaBaseClass, final String javaMethodName) {
        return resolveCommentFromClassHierarchy(javaBaseClass, new CommentExtractor() {
            @Override
            public String comment(ClassJavadoc classJavadoc) {
                return classJavadoc.getMethodComment(javaMethodName);
            }
        });
    }

    @Override
    public String resolveMethodTag(Class<?> javaBaseClass, final String javaMethodName,
            final String tagName) {
        return resolveCommentFromClassHierarchy(javaBaseClass, new CommentExtractor() {
            @Override
            public String comment(ClassJavadoc classJavadoc) {
                return classJavadoc.getMethodTag(javaMethodName, tagName);
            }
        });
    }

    @Override
    public String resolveMethodParameterComment(Class<?> javaBaseClass, final String javaMethodName,
            final String javaParameterName) {
        return resolveCommentFromClassHierarchy(javaBaseClass, new CommentExtractor() {
            @Override
            public String comment(ClassJavadoc classJavadoc) {
                return classJavadoc.getMethodParameterComment(javaMethodName, javaParameterName);
            }
        });
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
            ClassJavadoc classJavadoc = readJson(new File(relativePath));
            if (classJavadoc != null) {
                return classJavadoc;
            }
        } else {
            // Try to find the file in all configured directories.
            for (File dir : absoluteBaseDirs) {
                ClassJavadoc classJavadoc = readJson(new File(dir, relativePath));
                if (classJavadoc != null) {
                    return classJavadoc;
                }
            }
        }

        // might be in some jar on the classpath
        URL url = getClass().getClassLoader().getResource(relativePath);
        if (url != null) {
            return readJson(url);
        }

        log.warn("No Javadoc found for class {}", clazz.getCanonicalName());
        return new ClassJavadoc();
    }

    private ClassJavadoc readJson(File docSource) {
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

    private ClassJavadoc readJson(URL docSource) {
        try {
            return mapper
                    .readerFor(ClassJavadoc.class)
                    .readValue(docSource);
        } catch (IOException e) {
            log.error("Failed to read url {}", docSource, e);
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

    /**
     * Walks up the class hierarchy and interfaces until a comment is found or top most class is
     * reached.
     * <p>
     * Javadoc on super classes and Javadoc on interfaces of super classes has precedence
     * over the Javadoc on direct interfaces of the class. This is only important in the rare
     * case of competing Javadoc comments.
     * <p>
     * As we do not know the full method signature here, we can not check
     * whether a method in the super class actually overwrites the given method.
     * However, the Javadoc model ignores method signatures anyway and it
     * should not cause issues for the usual use case.
     */
    private String resolveCommentFromClassHierarchy(Class<?> javaBaseClass,
            CommentExtractor commentExtractor) {
        String comment = commentExtractor.comment(classJavadoc(javaBaseClass));
        if (isNotBlank(comment)) {
            // Direct Javadoc on a method always wins.
            return comment;
        }
        // Super class has precedence over interfaces, but this also means that interfaces
        // of super classes have precedence over interfaces of the class itself.
        if (javaBaseClass.getSuperclass() != null) {
            String superClassComment =
                    resolveCommentFromClassHierarchy(javaBaseClass.getSuperclass(),
                            commentExtractor);
            if (isNotBlank(superClassComment)) {
                return superClassComment;
            }
        }
        for (Class<?> i : javaBaseClass.getInterfaces()) {
            String interfaceComment = resolveCommentFromClassHierarchy(i, commentExtractor);
            if (isNotBlank(interfaceComment)) {
                return interfaceComment;
            }
        }
        return "";
    }

    private interface CommentExtractor {
        String comment(ClassJavadoc classJavadoc);
    }
}
