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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class JacksonRequestFieldSnippet extends AbstractJacksonFieldSnippet {

    public static final String REQUEST_FIELDS = "request-fields";
    private Type requestBodyType;

    public JacksonRequestFieldSnippet() {
        this(null);
    }

    protected JacksonRequestFieldSnippet(Type requestBodyType) {
        super(REQUEST_FIELDS);
        this.requestBodyType = requestBodyType;
    }

    public JacksonRequestFieldSnippet requestBodyAsType(Type requestBodyType) {
        return new JacksonRequestFieldSnippet(requestBodyType);
    }

    @Override
    protected Type getType(HandlerMethod method) {
        if (requestBodyType != null) {
            return requestBodyType;
        }

        for (MethodParameter param : method.getMethodParameters()) {
            if (isRequestBody(param) || isModelAttribute(param)) {
                return getType(param);
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

    private Type getType(final MethodParameter param) {
        if (isCollection(param.getParameterType())) {
            return new GenericArrayType() {

                @Override
                public Type getGenericComponentType() {
                    return firstGenericType(param);
                }
            };
        } else {
            return param.getParameterType();
        }
    }

    @Override
    public String getHeader() {
        return "Request fields";
    }
}
