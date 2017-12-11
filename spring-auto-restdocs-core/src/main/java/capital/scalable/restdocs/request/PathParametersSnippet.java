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

package capital.scalable.restdocs.request;

import capital.scalable.restdocs.OperationAttributeHelper;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.web.bind.annotation.PathVariable;

public class PathParametersSnippet extends AbstractParameterSnippet<PathVariable> {

    public static final String PATH_PARAMETERS = "auto-path-parameters";
    private final boolean failOnUndocumentedParams;

    public PathParametersSnippet() {
        this(false);
    }

    public PathParametersSnippet(boolean failOnUndocumentedParams) {
        super(PATH_PARAMETERS, null);
        this.failOnUndocumentedParams = failOnUndocumentedParams;
    }

    public PathParametersSnippet failOnUndocumentedParams(boolean failOnUndocumentedParams) {
        return new PathParametersSnippet(failOnUndocumentedParams);
    }

    @Override
    protected boolean isRequired(MethodParameter param, PathVariable annot) {
        // Spring disallows null for primitive types
        return param.getParameterType().isPrimitive() || annot.required();
    }

    @Override
    protected String getPath(PathVariable annot) {
        return annot.value();
    }

    protected PathVariable getAnnotation(MethodParameter param) {
        return param.getParameterAnnotation(PathVariable.class);
    }

    @Override
    public String getHeader() {
        return "Path parameters";
    }

    @Override
    protected boolean shouldFailOnUndocumentedParams() {
        return failOnUndocumentedParams;
    }

    @Override
    protected boolean removeParam(FieldDescriptor descriptor, Operation operation) {
        String requestPattern = OperationAttributeHelper.getRequestPattern(operation);
        String pattern = "{" + descriptor.getPath() + "}";
        return !requestPattern.contains(pattern);
    }

    @Override
    protected String getDefaultValue(final PathVariable annotation) {
        // @PathVariable does not have a default value
        return null;
    }
}
