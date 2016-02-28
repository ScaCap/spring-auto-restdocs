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

package capital.scalable.restdocs.jackson.payload;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;

/**
 * @author Florian Benz, Juraj Misur
 */
public class JacksonResponseFieldSnippet extends AbstractJacksonFieldSnippet {

    protected JacksonResponseFieldSnippet() {
        super("response");
    }

    @Override
    protected Type getType(final HandlerMethod method) {
        Class<?> returnType = method.getReturnType().getParameterType();
        if (returnType == ResponseEntity.class) {
            return firstGenericType(method.getReturnType());
        } else if (returnType == Page.class) {
            return firstGenericType(method.getReturnType());
        } else if (returnType == List.class) {
            return new GenericArrayType() {

                @Override
                public Type getGenericComponentType() {
                    return firstGenericType(method.getReturnType());
                }
            };
        } else {
            return returnType;
        }
    }

    @Override
    protected void enrichModel(MvcResult result, Map<String, Object> model) {
        Object handler = result.getHandler();
        final String infoText;
        if (handler != null && handler instanceof HandlerMethod
                && ((HandlerMethod) handler).getReturnType().getParameterType() == Page.class) {
            infoText = "Standard <<overview-pagination,Paging>> response where `content` field"
                    + " is list of following objects:";
        } else {
            infoText = "";
        }
        model.put("paginationInfo", infoText);
    }
}
