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

public interface JavadocReader {
    String resolveFieldComment(Class<?> javaBaseClass, String javaFieldName);

    String resolveFieldTag(Class<?> javaBaseClass, String javaFieldName, String tagName);

    String resolveMethodComment(Class<?> javaBaseClass, String javaMethodName);

    String resolveMethodParameterComment(Class<?> javaBaseClass, String javaMethodName,
            String javaParameterName);

    String resolveMethodTag(Class<?> javaBaseClass, String javaMethodName, String tagName);
}
