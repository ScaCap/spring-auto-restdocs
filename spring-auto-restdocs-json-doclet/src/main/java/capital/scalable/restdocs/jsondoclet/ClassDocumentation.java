/*-
 * #%L
 * Spring Auto REST Docs Json Doclet
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

public final class ClassDocumentation {
    private String comment = "";
    private final Map<String, FieldDocumentation> fields = new HashMap<>();
    private final Map<String, MethodDocumentation> methods = new HashMap<>();

    private ClassDocumentation() {
        // enforce usage of static factory method
    }

    public static ClassDocumentation fromClassDoc(ClassDoc classDoc) {
        ClassDocumentation cd = new ClassDocumentation();
        cd.setComment(classDoc.commentText());
        for (FieldDoc fieldDoc : classDoc.fields(false)) {
            cd.addField(fieldDoc);
        }
        for (MethodDoc methodDoc : classDoc.methods(false)) {
            cd.addMethod(methodDoc);
        }
        return cd;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    private void addField(FieldDoc fieldDoc) {
        this.fields.put(fieldDoc.name(), FieldDocumentation.fromFieldDoc(fieldDoc));
    }

    private void addMethod(MethodDoc methodDoc) {
        this.methods.put(methodDoc.name(), MethodDocumentation.fromMethodDoc(methodDoc));
    }
}
