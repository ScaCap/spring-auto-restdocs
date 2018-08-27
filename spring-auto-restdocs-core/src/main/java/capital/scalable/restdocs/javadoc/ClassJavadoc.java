/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClassJavadoc {
    private String comment;
    private Map<String, List<String>> tags = new HashMap<>();
    private Map<String, FieldJavadoc> fields = new HashMap<>();
    private Map<String, MethodJavadoc> methods = new HashMap<>();

    public String getClassComment() {
        return comment;
    }

    public String getFieldComment(String fieldName) {
        FieldJavadoc fieldJavadoc = fields.get(fieldName);
        return fieldJavadoc != null ? trimToEmpty(fieldJavadoc.getComment()) : "";
    }

    public String getMethodComment(String methodName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        return methodJavadoc != null ? trimToEmpty(methodJavadoc.getComment()) : "";
    }

    public String getMethodParameterComment(String methodName, String parameterName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        return methodJavadoc != null ? trimToEmpty(methodJavadoc.getParameterComment(parameterName)) : "";
    }

    public String getMethodTag(String javaMethodName, String tagName) {
        MethodJavadoc methodJavadoc = methods.get(javaMethodName);
        return methodJavadoc != null ? singleValueOrEmpty(methodJavadoc.getTag(tagName)) : "";
    }

    public String getFieldTag(String javaFieldName, String tagName) {
        FieldJavadoc fieldJavadoc = fields.get(javaFieldName);
        return fieldJavadoc != null ? singleValueOrEmpty(fieldJavadoc.getTag(tagName)) : "";
    }

    public String getClassTag(String tagName) {
        return singleValueOrEmpty(tags.get(tagName));
    }

    public Collection<String> getClassTags(String tagName) {
        return tags.get(tagName) != null ? tags.get(tagName) : Collections.emptyList();
    }

    private String singleValueOrEmpty(List<String> values) {
        return values == null || values.isEmpty() ? "" : values.get(0).trim();
    }

    static class MethodJavadoc {
        private String comment;
        private Map<String, String> parameters = new HashMap<>();
        private Map<String, List<String>> tags = new HashMap<>();

        public String getComment() {
            return comment;
        }

        public String getParameterComment(String parameterName) {
            return parameters.get(parameterName);
        }

        public List<String> getTag(String tagName) {
            return tags.get(tagName);
        }
    }

    static class FieldJavadoc {
        private String comment;
        private Map<String, List<String>> tags = new HashMap<>();

        public String getComment() {
            return comment;
        }

        public List<String> getTag(String tagName) {
            return tags.get(tagName);
        }
    }
}
