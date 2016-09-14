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

package capital.scalable.restdocs.jackson;

import java.lang.annotation.Annotation;
import java.util.List;

class InternalFieldInfo {
    private final Class<?> javaBaseClass;
    private final String javaFieldName;
    private final String jsonFieldPath;
    private List<Annotation> annotations;
    private final boolean shouldExpand;

    public InternalFieldInfo(Class<?> javaBaseClass, String javaFieldName,
            String jsonFieldPath, List<Annotation> annotations, boolean shouldExpand) {
        this.javaBaseClass = javaBaseClass;
        this.javaFieldName = javaFieldName;
        this.jsonFieldPath = jsonFieldPath;
        this.annotations = annotations;
        this.shouldExpand = shouldExpand;
    }

    public Class<?> getJavaBaseClass() {
        return javaBaseClass;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    public String getJsonFieldPath() {
        return jsonFieldPath;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public boolean shouldExpand() {
        return shouldExpand;
    }
}
