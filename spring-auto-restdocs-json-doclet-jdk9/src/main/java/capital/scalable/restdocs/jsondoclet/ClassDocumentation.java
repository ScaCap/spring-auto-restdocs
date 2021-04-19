/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
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

import static java.util.Optional.ofNullable;
import static capital.scalable.restdocs.jsondoclet.DocletUtils.cleanupDocComment;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.ParamTree;

import javax.lang.model.element.Element;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jdk.javadoc.doclet.DocletEnvironment;

public final class ClassDocumentation {
    private String comment = "";
    private final Map<String, FieldDocumentation> fields = new HashMap<>();
    private final Map<String, MethodDocumentation> methods = new HashMap<>();

    private ClassDocumentation() {
        // enforce usage of static factory method
    }

    public static ClassDocumentation fromClassDoc(DocletEnvironment docEnv,
            Element element) {
        ClassDocumentation cd = new ClassDocumentation();
        cd.setComment(cleanupDocComment(docEnv.getElementUtils().getDocComment(element)));

        if ("RECORD".equals(element.getKind().name())) {
            ofNullable(docEnv.getDocTrees().getDocCommentTree(element))
                .stream()
                .map(DocCommentTree::getBlockTags)
                .flatMap(List::stream)
                .filter(ParamTree.class::isInstance)
                .map(ParamTree.class::cast)
                .filter(p -> !p.isTypeParameter())
                .forEach(p -> {
                    String name = p.getName().getName().toString();
                    String desc = p.getDescription()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(" "))
                        ;

                    cd.fields.put(name, FieldDocumentation.fromString(desc));
                })
                ;
        } else {
            element.getEnclosedElements().forEach(fieldOrMethod -> {
                switch (fieldOrMethod.getKind()) {
                    case FIELD:
                        cd.addField(docEnv, fieldOrMethod);
                        break;
                    case METHOD:
                    case CONSTRUCTOR:
                        cd.addMethod(docEnv, fieldOrMethod);
                        break;
                    default:
                        // Ignored
                        break;
                }
            });
        }

        return cd;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    private void addField(DocletEnvironment docEnv, Element element) {
        this.fields.put(element.getSimpleName().toString(),
                FieldDocumentation.fromFieldDoc(docEnv, element));
    }

    private void addMethod(DocletEnvironment docEnv, Element element) {
        this.methods.put(element.getSimpleName().toString(),
                MethodDocumentation.fromMethodDoc(docEnv, element));
    }

}
