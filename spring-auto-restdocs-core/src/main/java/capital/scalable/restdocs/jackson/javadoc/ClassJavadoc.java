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

package capital.scalable.restdocs.jackson.javadoc;

import static org.springframework.util.StringUtils.hasText;

import java.util.HashMap;
import java.util.Map;

class ClassJavadoc {
    private String comment;
    private Map<String, String> fields = new HashMap<>();
    private Map<String, MethodJavadoc> methods = new HashMap<>();

    public String getClassComment() {
        return comment;
    }

    public String getFieldComment(String fieldName) {
        return trimToEmpty(fields.get(fieldName));
    }

    public String getMethodComment(String methodName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return methodJavadoc.getComment();
        } else {
            return "";
        }
    }

    public String getMethodParameterComment(String methodName, String parameterName) {
        MethodJavadoc methodJavadoc = methods.get(methodName);
        if (methodJavadoc != null) {
            return methodJavadoc.getParameterComment(parameterName);
        } else {
            return "";
        }
    }

    private static String trimToEmpty(String value) {
        return hasText(value) ? value : "";
    }

    static class MethodJavadoc {
        private String comment;
        private Map<String, String> parameters = new HashMap<>();

        public String getComment() {
            return trimToEmpty(comment);
        }

        public String getParameterComment(String parameterName) {
            return trimToEmpty(parameters.get(parameterName));
        }
    }
}
