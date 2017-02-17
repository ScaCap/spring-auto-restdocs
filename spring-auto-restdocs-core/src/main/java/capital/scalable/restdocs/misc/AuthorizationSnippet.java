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

package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.getAuthorization;
import static capital.scalable.restdocs.OperationAttributeHelper.setAuthorization;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class AuthorizationSnippet extends TemplatedSnippet implements SectionSupport {

    public static final String AUTHORIZATION = "authorization";
    private final String defaultAuthorization;

    public AuthorizationSnippet(String defaultAuthorization) {
        super(AUTHORIZATION, null);
        this.defaultAuthorization = defaultAuthorization;
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
        model.put("authorization", authorizationDescription(operation));
        return model;
    }

    private String authorizationDescription(Operation operation) {
        String requestAuthorization = getAuthorization(operation);
        if (requestAuthorization != null) {
            return requestAuthorization;
        } else {
            return defaultAuthorization;
        }
    }

    public static MockHttpServletRequest documentAuthorization(MockHttpServletRequest request,
            String authorization) {
        setAuthorization(request, authorization);
        return request;
    }

    @Override
    public String getFileName() {
        return getSnippetName();
    }

    @Override
    public String getHeader() {
        return "Authorization";
    }

    @Override
    public boolean hasContent(Operation operation) {
        return true; // if this snippet is included, always print at least default value
    }
}