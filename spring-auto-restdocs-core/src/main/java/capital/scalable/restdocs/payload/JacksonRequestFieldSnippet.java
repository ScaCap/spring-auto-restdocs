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
package capital.scalable.restdocs.payload;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_REQUEST_FIELDS;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class JacksonRequestFieldSnippet extends AbstractJacksonFieldSnippet {

    private final Type requestBodyType;
    private final boolean failOnUndocumentedFields;

    public JacksonRequestFieldSnippet() {
        this(null, false);
    }

    public JacksonRequestFieldSnippet(Type requestBodyType, boolean failOnUndocumentedFields) {
        super(AUTO_REQUEST_FIELDS, null);
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
            return (GenericArrayType) () -> firstGenericType(param);
        } else {
            return param.getParameterType();
        }
    }

    @Override
    public String getHeaderKey() {
        return "request-fields";
    }

    @Override
    protected boolean shouldFailOnUndocumentedFields() {
        return failOnUndocumentedFields;
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[]{
                "th-path",
                "th-type",
                "th-optional",
                "th-description",
                "no-request-body"
        };
    }
}
