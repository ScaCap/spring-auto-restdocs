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

package capital.scalable.jsondoclet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;

/**
 * JavaDoc to JSON doclet.
 */
public class ExtractDocumentationAsJsonDoclet {

    public static boolean start(RootDoc root) throws IOException {
        for (ClassDoc classDoc : root.classes()) {
            ClassDocumentation cd = ClassDocumentation.fromClassDoc(classDoc);
            writeToFile(cd, classDoc);
        }
        return true;
    }

    private static void writeToFile(ClassDocumentation cd, ClassDoc classDoc) throws IOException {
        String fileName = classDoc.name() + ".json";
        String packageName = classDoc.containingPackage().name();
        String packageDir = packageName.replace(".", File.separator);
        Path packagePath = Paths.get(packageDir);
        Files.createDirectories(packagePath);
        cd.writeToFile(packagePath.resolve(fileName).toFile());
    }

    private static class ClassDocumentation {
        private String comment = "";
        private Map<String, String> fields = new HashMap<>();
        private Map<String, MethodDocumentation> methods = new HashMap<>();

        private ClassDocumentation() {
            // enforce usage of static factory method
        }

        private static ClassDocumentation fromClassDoc(ClassDoc classDoc) {
            ClassDocumentation cd = new ClassDocumentation();
            cd.setComment(classDoc.commentText());
            for (FieldDoc fieldDoc : classDoc.fields(false)) {
                cd.addField(fieldDoc.name(), fieldDoc.commentText());
            }
            for (MethodDoc methodDoc : classDoc.methods(false)) {
                cd.addMethod(methodDoc);
            }
            return cd;
        }

        private void setComment(String comment) {
            this.comment = escape(comment);
        }

        private void addField(String name, String comment) {
            this.fields.put(name, escape(comment));
        }

        private void addMethod(MethodDoc methodDoc) {
            this.methods.put(methodDoc.name(), MethodDocumentation.fromMethodDoc(methodDoc));
        }

        public void writeToFile(File file) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.append(toJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String toJson() {
            StringBuilder builder = new StringBuilder();
            builder.append("{\"comment\":\"");
            builder.append(comment);
            builder.append("\",\"fields\":{");
            for (Entry<String, String> e : fields.entrySet()) {
                builder.append("\"");
                builder.append(e.getKey());
                builder.append("\":\"");
                builder.append(e.getValue());
                builder.append("\",");
            }
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("}");
            builder.append(",\"methods\":{");
            for (Entry<String, MethodDocumentation> e : methods.entrySet()) {
                builder.append("\"");
                builder.append(e.getKey());
                builder.append("\": ");
                builder.append(e.getValue().toJson());
                builder.append(",");
            }
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("}}");
            return builder.toString();
        }
    }

    private static class MethodDocumentation {
        private String comment;
        private Map<String, String> parameters = new HashMap<>();

        private static MethodDocumentation fromMethodDoc(MethodDoc methodDoc) {
            MethodDocumentation md = new MethodDocumentation();
            md.comment = escape(methodDoc.commentText());

            for (ParamTag param : methodDoc.paramTags()) {
                md.parameters.put(param.parameterName(), escape(param.parameterComment()));
            }
            return md;
        }

        public String toJson() {
            StringBuilder builder = new StringBuilder();
            builder.append("{ \"comment\":\"");
            builder.append(comment);
            builder.append("\", \"parameters\":{");
            for (Entry<String, String> e : parameters.entrySet()) {
                builder.append("\"");
                builder.append(e.getKey());
                builder.append("\":\"");
                builder.append(e.getValue());
                builder.append("\",");
            }
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("}}");
            return builder.toString();
        }
    }

    static String escape(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}