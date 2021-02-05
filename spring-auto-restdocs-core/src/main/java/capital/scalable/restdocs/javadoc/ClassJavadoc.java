/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.javadoc;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.HashMap;
import java.util.Map;

class ClassJavadoc {
    private String comment;
    private Map<String, FieldJavadoc> fields = new HashMap<>();
    private Map<String, MethodJavadoc> methods = new HashMap<>();

    public String getClassComment() {
        return comment;
    }

    public String getFieldComment(String fieldName) {
        FieldJavadoc fieldJavadoc = fields.get(fieldName);
        if (fieldJavadoc != null) {
            return trimToEmpty(fieldJavadoc.getComment());
        } else {
            return "";
        }
    }

    public String getMethodComment(String methodName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return trimToEmpty(methodJavadoc.getComment());
        } else {
            return "";
        }
    }

    public String getMethodParameterComment(String methodName, String parameterName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return trimToEmpty(methodJavadoc.getParameterComment(parameterName));
        } else {
            return "";
        }
    }

    public String getMethodTag(String javaMethodName, String tagName) {
        MethodJavadoc methodJavadoc = methods.get(javaMethodName);
        if (methodJavadoc != null) {
            return trimToEmpty(methodJavadoc.getTag(tagName));
        } else {
            return "";
        }
    }

    public String getFieldTag(String javaFieldName, String tagName) {
        FieldJavadoc fieldJavadoc = fields.get(javaFieldName);
        if (fieldJavadoc != null) {
            return trimToEmpty(fieldJavadoc.getTag(tagName));
        } else {
            return "";
        }
    }

    static class MethodJavadoc {
        private String comment;
        private Map<String, String> parameters = new HashMap<>();
        private Map<String, String> tags = new HashMap<>();

        public String getComment() {
            return comment;
        }

        public String getParameterComment(String parameterName) {
            return parameters.get(parameterName);
        }

        public String getTag(String tagName) {
            return tags.get(tagName);
        }
    }

    static class FieldJavadoc {
        private String comment;
        private Map<String, String> tags = new HashMap<>();

        public String getComment() {
            return comment;
        }

        public String getTag(String tagName) {
            return tags.get(tagName);
        }
    }
}
