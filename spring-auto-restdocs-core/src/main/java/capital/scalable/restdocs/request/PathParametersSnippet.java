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
package capital.scalable.restdocs.request;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_PATH_PARAMETERS;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.web.bind.annotation.PathVariable;

public class PathParametersSnippet extends AbstractParameterSnippet<PathVariable> {

    private final boolean failOnUndocumentedParams;

    public PathParametersSnippet() {
        this(false);
    }

    public PathParametersSnippet(boolean failOnUndocumentedParams) {
        super(AUTO_PATH_PARAMETERS, null);
        this.failOnUndocumentedParams = failOnUndocumentedParams;
    }

    public PathParametersSnippet failOnUndocumentedParams(boolean failOnUndocumentedParams) {
        return new PathParametersSnippet(failOnUndocumentedParams);
    }

    @Override
    protected boolean isRequired(MethodParameter param, PathVariable annot) {
        // Spring disallows null for primitive types.
        // For types wrapped in Optional or nullable Kotlin types, the required flag in
        // the annotation is ignored by Spring.
        return param.getParameterType().isPrimitive() || (!param.isOptional() && annot.required());
    }

    @Override
    protected String getPath(PathVariable annot) {
        return annot.value();
    }

    protected PathVariable getAnnotation(MethodParameter param) {
        return param.getParameterAnnotation(PathVariable.class);
    }

    @Override
    public String getHeaderKey(Operation operation) {
        return "path-parameters";
    }

    @Override
    protected boolean shouldFailOnUndocumentedParams() {
        return failOnUndocumentedParams;
    }

    @Override
    protected String getDefaultValue(final PathVariable annotation) {
        // @PathVariable does not have a default value
        return null;
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[]{
                "th-parameter",
                "th-type",
                "th-optional",
                "th-description",
                "no-params"
        };
    }
}
