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

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

public class RequestParametersSnippet extends AbstractParameterSnippet<RequestParam> {

    public static final String REQUEST_PARAMETERS = "request-parameters";

    private final boolean failOnUndocumentedParams;

    public RequestParametersSnippet() {
        this(false);
    }

    public RequestParametersSnippet(boolean failOnUndocumentedParams) {
        super(REQUEST_PARAMETERS, null);
        this.failOnUndocumentedParams = failOnUndocumentedParams;
    }

    public RequestParametersSnippet failOnUndocumentedParams(boolean failOnUndocumentedParams) {
        return new RequestParametersSnippet(failOnUndocumentedParams);
    }

    @Override
    protected boolean isRequired(MethodParameter param, RequestParam annot) {
        return param.getParameterType().isPrimitive()
                ? ValueConstants.DEFAULT_NONE.equals(annot.defaultValue())
                : annot.required();
    }

    @Override
    protected String getPath(RequestParam annot) {
        return annot.value();
    }

    protected RequestParam getAnnotation(MethodParameter param) {
        return param.getParameterAnnotation(RequestParam.class);
    }

    @Override
    public String getHeader() {
        return "Query parameters";
    }

    @Override
    protected boolean shouldFailOnUndocumentedParams() {
        return failOnUndocumentedParams;
    }
}
