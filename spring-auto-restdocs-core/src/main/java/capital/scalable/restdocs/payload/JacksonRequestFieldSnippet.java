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

package capital.scalable.restdocs.payload;

import java.lang.reflect.Type;

import capital.scalable.restdocs.util.TypeUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class JacksonRequestFieldSnippet extends AbstractJacksonFieldSnippet {

    public static final String REQUEST_FIELDS = "auto-request-fields";

    private final Type requestBodyType;
    private final boolean failOnUndocumentedFields;

    public JacksonRequestFieldSnippet() {
        this(null, false);
    }

    public JacksonRequestFieldSnippet(Type requestBodyType, boolean failOnUndocumentedFields) {
        super(REQUEST_FIELDS, null);
        this.requestBodyType = requestBodyType;
        this.failOnUndocumentedFields = failOnUndocumentedFields;
    }

    public JacksonRequestFieldSnippet requestBodyAsType(Type requestBodyType) {
        return new JacksonRequestFieldSnippet(requestBodyType, failOnUndocumentedFields);
    }

    public JacksonRequestFieldSnippet failOnUndocumentedFields(boolean failOnUndocumentedFields) {
        return new JacksonRequestFieldSnippet(requestBodyType, failOnUndocumentedFields);
    }

    @Override
    protected Type getType(HandlerMethod method) {
        if (requestBodyType != null) {
            return requestBodyType;
        }

        for (MethodParameter param : method.getMethodParameters()) {
            if (isRequestBody(param) || isModelAttribute(param)) {
                return TypeUtil.getType(param);
            }
        }
        return null;
    }

    private boolean isRequestBody(MethodParameter param) {
        return param.getParameterAnnotation(RequestBody.class) != null;
    }

    private boolean isModelAttribute(MethodParameter param) {
        return param.getParameterAnnotation(ModelAttribute.class) != null;
    }

    @Override
    public String getHeader() {
        return "Request fields";
    }

    @Override
    protected boolean shouldFailOnUndocumentedFields() {
        return failOnUndocumentedFields;
    }
}
