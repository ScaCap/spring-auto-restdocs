/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;

public class RequestParametersSnippet extends AbstractParameterSnippet<RequestParam> {

    public static final String REQUEST_PARAMETERS = "auto-request-parameters";
    public static final String SPRING_DATA_PAGEABLE_CLASS =
            "org.springframework.data.domain.Pageable";

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
        if (hasDefaultValue(annot)) {
            // Having a defaultValue set implies required=false
            return false;
        } else if (param.getParameterType().isPrimitive()) {
            // A primitive type is required if no defaultValue is set, regardless of the value of
            // the required flag
            return true;
        } else {
            // For Types wrapped in Optional or nullable Kotlin types, the required flag in
            // the annotation is ignored by Spring.
            return !param.isOptional() && annot.required();
        }
    }

    private static boolean hasDefaultValue(RequestParam annot) {
        return !ValueConstants.DEFAULT_NONE.equals(annot.defaultValue());
    }

    @Override
    protected String getPath(RequestParam annot) {
        return annot.value();
    }

    protected RequestParam getAnnotation(MethodParameter param) {
        return param.getParameterAnnotation(RequestParam.class);
    }

    @Override
    protected void enrichModel(Map<String, Object> model, HandlerMethod handlerMethod) {
        boolean isPageRequest = isPageRequest(handlerMethod);
        model.put("isPageRequest", isPageRequest);
        if (isPageRequest) {
            model.put("noContent", false);
        }
    }

    private boolean isPageRequest(HandlerMethod method) {
        for (MethodParameter param : method.getMethodParameters()) {
            if (isPageable(param)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPageable(MethodParameter param) {
        return SPRING_DATA_PAGEABLE_CLASS.equals(
                param.getParameterType().getCanonicalName());
    }

    @Override
    public String getHeaderKey() {
        return "request-parameters";
    }

    @Override
    protected boolean shouldFailOnUndocumentedParams() {
        return failOnUndocumentedParams;
    }

    @Override
    protected String getDefaultValue(final RequestParam annotation) {
        return annotation.defaultValue();
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[]{
                "th-parameter",
                "th-type",
                "th-optional",
                "th-description",
                "pagination-request-adoc",
                "pagination-request-md",
                "no-params"
        };
    }
}
