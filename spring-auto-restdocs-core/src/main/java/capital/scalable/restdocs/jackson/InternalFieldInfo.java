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
package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.databind.JavaType;

class InternalFieldInfo {
    private final Class<?> javaBaseClass;
    private final String javaFieldName;
    private final JavaType javaFieldType;
    private final String jsonFieldPath;
    private final boolean shouldExpand;
    /**
     * True for required bean properties like fields annotated with @JsonProperty(required = true)
     * or non-null properties in Kotlin.
     */
    private final boolean required;

    public InternalFieldInfo(Class<?> javaBaseClass, String javaFieldName, JavaType javaFieldType,
            String jsonFieldPath, boolean shouldExpand, boolean required) {
        this.javaBaseClass = javaBaseClass;
        this.javaFieldName = javaFieldName;
        this.javaFieldType = javaFieldType;
        this.jsonFieldPath = jsonFieldPath;
        this.shouldExpand = shouldExpand;
        this.required = required;
    }

    public Class<?> getJavaBaseClass() {
        return javaBaseClass;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    public JavaType getJavaFieldType() {
        return javaFieldType;
    }

    public String getJsonFieldPath() {
        return jsonFieldPath;
    }

    public boolean shouldExpand() {
        return shouldExpand;
    }

    public boolean isRequired() {
        return required;
    }
}
