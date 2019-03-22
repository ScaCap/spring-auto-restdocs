/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
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
package capital.scalable.restdocs.jsondoclet;

import static capital.scalable.restdocs.jsondoclet.DocletUtils.cleanupDocComment;
import static capital.scalable.restdocs.jsondoclet.DocletUtils.cleanupTagName;
import static capital.scalable.restdocs.jsondoclet.DocletUtils.cleanupTagValue;

import javax.lang.model.element.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ParamTree;
import jdk.javadoc.doclet.DocletEnvironment;

public class MethodDocumentation {
    private String comment = "";
    private final Map<String, String> parameters = new HashMap<>();
    private final Map<String, String> tags = new HashMap<>();

    private MethodDocumentation() {
        // enforce usage of static factory method
    }

    public static MethodDocumentation fromMethodDoc(DocletEnvironment docEnv,
            Element methodElement) {
        MethodDocumentation md = new MethodDocumentation();
        md.comment = cleanupDocComment(docEnv.getElementUtils().getDocComment(methodElement));

        Optional.ofNullable(docEnv.getDocTrees().getDocCommentTree(methodElement))
                .ifPresent(docCommentTree -> docCommentTree.getBlockTags().forEach(tag -> {
                    if (tag.getKind().equals(DocTree.Kind.PARAM)) {
                        ParamTree paramTag = (ParamTree) tag;
                        md.parameters.put(paramTag.getName().toString(),
                                paramTag.getDescription().toString());
                    } else {
                        md.tags.put(
                                cleanupTagName(((BlockTagTree) tag).getTagName()),
                                cleanupTagValue(tag.toString()));
                    }
                }));

        return md;
    }

}
