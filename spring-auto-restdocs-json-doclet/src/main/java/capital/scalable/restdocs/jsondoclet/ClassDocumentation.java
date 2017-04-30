/*
 * Copyright 2017 the original author or authors.
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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

public final class ClassDocumentation {
    private String comment = "";
    private final Map<String, String> fields = new HashMap<>();
    private final Map<String, MethodDocumentation> methods = new HashMap<>();

    private ClassDocumentation() {
        // enforce usage of static factory method
    }

    public static ClassDocumentation fromClassDoc(ClassDoc classDoc) {
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
        this.comment = JsonUtils.escape(comment);
    }

    private void addField(String name, String comment) {
        this.fields.put(name, JsonUtils.escape(comment));
    }

    private void addMethod(MethodDoc methodDoc) {
        this.methods.put(methodDoc.name(), MethodDocumentation.fromMethodDoc(methodDoc));
    }

    public void writeToFile(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
            writer.write(toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"comment\":\"");
        builder.append(comment);
        builder.append("\",\"fields\":{");
        for (Map.Entry<String, String> e : fields.entrySet()) {
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
        for (Map.Entry<String, MethodDocumentation> e : methods.entrySet()) {
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
