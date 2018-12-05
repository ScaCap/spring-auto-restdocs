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
package capital.scalable.restdocs.payload;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_RESPONSE_FIELDS;
import static capital.scalable.restdocs.i18n.SnippetTranslationResolver.translate;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Map;

import capital.scalable.restdocs.jackson.FieldDescriptors;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;

public class JacksonResponseFieldSnippet extends AbstractJacksonFieldSnippet {

    public static final String SPRING_DATA_PAGE_CLASS = "org.springframework.data.domain.Page";
    public static final String REACTOR_MONO_CLASS = "reactor.core.publisher.Mono";
    public static final String REACTOR_FLUX_CLASS = "reactor.core.publisher.Flux";

    private final Type responseBodyType;
    private final boolean failOnUndocumentedFields;

    public JacksonResponseFieldSnippet() {
        this(null, false);
    }

    public JacksonResponseFieldSnippet(Type responseBodyType, boolean failOnUndocumentedFields) {
        super(AUTO_RESPONSE_FIELDS, null);
        this.responseBodyType = responseBodyType;
        this.failOnUndocumentedFields = failOnUndocumentedFields;
    }

    public JacksonResponseFieldSnippet responseBodyAsType(Type responseBodyType) {
        return new JacksonResponseFieldSnippet(responseBodyType, failOnUndocumentedFields);
    }

    public JacksonResponseFieldSnippet failOnUndocumentedFields(boolean failOnUndocumentedFields) {
        return new JacksonResponseFieldSnippet(responseBodyType, failOnUndocumentedFields);
    }

    @Override
    protected Type getType(final HandlerMethod method) {
        if (responseBodyType != null) {
            return responseBodyType;
        }

        Class<?> returnType = method.getReturnType().getParameterType();
        if (returnType == ResponseEntity.class) {
            return firstGenericType(method.getReturnType());
        } else if (returnType == HttpEntity.class) {
            return firstGenericType(method.getReturnType());
        } else if (SPRING_DATA_PAGE_CLASS.equals(returnType.getCanonicalName())) {
            return firstGenericType(method.getReturnType());
        } else if (isCollection(returnType)) {
            return (GenericArrayType) () -> firstGenericType(method.getReturnType());
        } else if ("void".equals(returnType.getName())) {
            return null;
        } else if (REACTOR_MONO_CLASS.equals(returnType.getCanonicalName())) {
            return firstGenericType(method.getReturnType());
        } else if (REACTOR_FLUX_CLASS.equals(returnType.getCanonicalName())) {
            return (GenericArrayType) () -> firstGenericType(method.getReturnType());
        } else {
            return returnType;
        }
    }

    @Override
    protected void enrichModel(Map<String, Object> model, HandlerMethod handlerMethod,
            FieldDescriptors fieldDescriptors) {
        model.put("isPageResponse", isPageResponse(handlerMethod));
        if (fieldDescriptors.getNoContentMessageKey() != null) {
            model.put("no-response-body", translate(fieldDescriptors.getNoContentMessageKey()));
        }
    }

    private boolean isPageResponse(HandlerMethod handlerMethod) {
        return SPRING_DATA_PAGE_CLASS.equals(
                handlerMethod.getReturnType().getParameterType().getCanonicalName());
    }

    @Override
    public String getHeaderKey() {
        return "response-fields";
    }

    @Override
    protected boolean shouldFailOnUndocumentedFields() {
        return failOnUndocumentedFields;
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[] {
                "th-path",
                "th-type",
                "th-optional",
                "th-description",
                "pagination-response-adoc",
                "pagination-response-md",
                "no-response-body"
        };
    }
}
