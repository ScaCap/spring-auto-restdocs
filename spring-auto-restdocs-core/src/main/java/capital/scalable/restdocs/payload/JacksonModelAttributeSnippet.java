/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import capital.scalable.restdocs.OperationAttributeHelper;
import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import capital.scalable.restdocs.jackson.FieldDescriptors;
import capital.scalable.restdocs.payload.AbstractJacksonFieldSnippet;
import capital.scalable.restdocs.request.RequestParametersSnippet;
import capital.scalable.restdocs.util.HandlerMethodUtil;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_MODELATTRIBUTE;

public class JacksonModelAttributeSnippet extends AbstractJacksonFieldSnippet {
    private final boolean failOnUndocumentedFields;
    private final Collection<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers;

    public JacksonModelAttributeSnippet() {
        this(null, false);
    }

    public JacksonModelAttributeSnippet(Collection<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers, boolean failOnUndocumentedFields) {
        super(AUTO_MODELATTRIBUTE, null);
        this.failOnUndocumentedFields = failOnUndocumentedFields;
        this.handlerMethodArgumentResolvers = handlerMethodArgumentResolvers;
    }

    @Override
    protected Type getType(HandlerMethod method) {
        for (MethodParameter param : method.getMethodParameters()) {
            if (isModelAttribute(param) || hasNoHandlerMethodArgumentResolver(param)) {
                return getType(param);
            }
        }
        return null;
    }


    private boolean isModelAttribute(MethodParameter param) {
        return param.getParameterAnnotation(ModelAttribute.class) != null;
    }

    private boolean hasNoHandlerMethodArgumentResolver(MethodParameter param) {
        return !BeanUtils.isSimpleProperty(param.getParameterType())
            &&  this.handlerMethodArgumentResolvers != null
            && this.handlerMethodArgumentResolvers
            .stream()
            .allMatch(obj -> !obj.supportsParameter(param));
    }


    private Type getType(final MethodParameter param) {
        if (isCollection(param.getParameterType())) {
            return (GenericArrayType) () -> firstGenericType(param);
        } else {
            return param.getParameterType();
        }
    }

    /**
     * is request method get supported
     * @param method
     * @return
     */
    protected boolean isRequestMethodGet(HandlerMethod method) {
        RequestMapping requestMapping = method.getMethodAnnotation(RequestMapping.class);
        return requestMapping == null
        || requestMapping.method() == null
        || Arrays.stream(requestMapping.method()).anyMatch(requestMethod -> {
            return requestMethod == RequestMethod.GET;
        });
    }

    @Override
    protected void enrichModel(Map<String, Object> model, HandlerMethod handlerMethod,
            FieldDescriptors fieldDescriptors, SnippetTranslationResolver translationResolver) {
        boolean isPageRequest = HandlerMethodUtil.isPageRequest(handlerMethod);
        model.put("isPageRequest", isPageRequest);
        if (isPageRequest) {
            model.put("noContent", false);
        }
    }

    @Override
    protected boolean shouldFailOnUndocumentedFields() {
        return failOnUndocumentedFields;
    }

    @Override
    public String getHeaderKey(Operation operation) {
        HandlerMethod method = OperationAttributeHelper.getHandlerMethod(operation);
        if (method != null && this.isRequestMethodGet(method)) {
            return "request-parameters";
        }
        return "request-fields";
    }
}
