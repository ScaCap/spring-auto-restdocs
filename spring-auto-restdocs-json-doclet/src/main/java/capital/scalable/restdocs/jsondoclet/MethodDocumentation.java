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

public class MethodDocumentation {
    private String comment;
    private final Map<String, String> parameters = new HashMap<>();

    private MethodDocumentation() {
        // enforce usage of static factory method
    }

    public static MethodDocumentation fromMethodDoc(MethodDoc methodDoc) {
        MethodDocumentation md = new MethodDocumentation();
        md.comment = JsonUtils.escape(methodDoc.commentText());

        for (ParamTag param : methodDoc.paramTags()) {
            md.parameters.put(param.parameterName(), JsonUtils.escape(param.parameterComment()));
        }
        return md;
    }

    public String toJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"comment\":\"");
        builder.append(comment);
        builder.append("\", \"parameters\":{");
        for (Map.Entry<String, String> e : parameters.entrySet()) {
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
