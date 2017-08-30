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

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;

public class MethodDocumentation {
    private String title = "";
    private String comment = "";
    private final Map<String, String> parameters = new HashMap<>();

    private MethodDocumentation() {
        // enforce usage of static factory method
    }

    public static MethodDocumentation fromMethodDoc(MethodDoc methodDoc) {
        MethodDocumentation md = new MethodDocumentation();
        md.comment = methodDoc.commentText();

        for (ParamTag param : methodDoc.paramTags()) {
            md.parameters.put(param.parameterName(), param.parameterComment());
        }
        Tag[] title = methodDoc.tags("@title");
        if (title.length > 0) {
            md.title = title[0].text();
        }

        return md;
    }
}
