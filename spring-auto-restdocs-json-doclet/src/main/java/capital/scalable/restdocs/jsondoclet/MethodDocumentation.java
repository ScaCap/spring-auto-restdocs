/*-
 * #%L
 * Spring Auto REST Docs Json Doclet
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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

import static capital.scalable.restdocs.jsondoclet.DocletUtils.cleanupTagName;

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;

public class MethodDocumentation {
    private String comment = "";
    private final Map<String, String> parameters = new HashMap<>();
    private final Map<String, String> tags = new HashMap<>();

    private MethodDocumentation() {
        // enforce usage of static factory method
    }

    public static MethodDocumentation fromMethodDoc(MethodDoc methodDoc) {
        MethodDocumentation md = new MethodDocumentation();
        md.comment = methodDoc.commentText();

        for (Tag tag : methodDoc.tags()) {
            if (tag instanceof ParamTag) {
                ParamTag paramTag = (ParamTag) tag;
                md.parameters.put(paramTag.parameterName(), paramTag.parameterComment());
            } else {
                md.tags.put(cleanupTagName(tag.name()), tag.text());
            }
        }

        return md;
    }
}
