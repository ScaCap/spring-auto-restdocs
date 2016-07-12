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

package capital.scalable.restdocs.jackson.jackson;

/**
 * @author Florian Benz
 */
class InternalFieldInfo {
    private final Class<?> javaBaseClass;
    private final String javaFieldName;
    private final String jsonFieldPath;
    private final boolean optional;
    private final String validationInformation;

    public InternalFieldInfo(Class<?> javaBaseClass, String javaFieldName,
            String jsonFieldPath, boolean optional, String validationInformation) {
        this.javaBaseClass = javaBaseClass;
        this.javaFieldName = javaFieldName;
        this.jsonFieldPath = jsonFieldPath;
        this.optional = optional;
        this.validationInformation = validationInformation;
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

    public boolean getOptional() {
        return optional;
    }

    public String getValidationInformation() {
        return validationInformation;
    }
}
