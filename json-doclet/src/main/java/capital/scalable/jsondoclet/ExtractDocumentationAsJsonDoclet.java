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

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.RootDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaDoc to JSON doclet.
 *
 * @author Florian Benz
 */
public class ExtractDocumentationAsJsonDoclet {

    private final static Logger log = LoggerFactory.getLogger(ExtractDocumentationAsJsonDoclet.class);

    public static boolean start(RootDoc root) {
        for (ClassDoc classDoc : root.classes()) {
            ClassDocumentation cd = ClassDocumentation.fromClassDoc(classDoc);
            if (cd.containsAtLeastOneComment()) {
                cd.writeToFile(classDoc.qualifiedName() + ".json");
            }
        }
        return true;
    }

    private static class ClassDocumentation {
        private String comment;
        private Map<String, String> fields = new HashMap<>();

        private ClassDocumentation() {
            // enforce usage of static factory method
        }

        private static ClassDocumentation fromClassDoc(ClassDoc classDoc) {
            ClassDocumentation cd = new ClassDocumentation();
            cd.setComment(classDoc.commentText());
            for (FieldDoc fieldDoc : classDoc.fields(false)) {
                cd.addField(fieldDoc.name(), fieldDoc.commentText());
            }
            return cd;
        }

        private void setComment(String comment) {
            this.comment = escape(comment);
        }

        private void addField(String name, String comment) {
            this.fields.put(name, escape(comment));
        }

        public boolean containsAtLeastOneComment() {
            if (this.comment != null && this.comment.length() > 0) {
                return true;
            }
            for (Entry<String, String> e : fields.entrySet()) {
                if (e.getValue() != null && e.getValue().length() > 0) {
                    return true;
                }
            }
            return false;
        }

        public void writeToFile(String fileName) {
            try (FileWriter fw = new FileWriter(fileName)) {
                fw.append(toJson());
            } catch (IOException e) {
                log.error("Failed to write file", e);
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
            builder.append("}}");
            return builder.toString();
        }

        private String escape(String text) {
            return text.replace("\"", "\\\"").replace("\n", "\\n");
        }
    }
}