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

import static capital.scalable.restdocs.OperationAttributeHelper.getDocumentationContext;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.springframework.util.StringUtils.capitalize;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolverFactory;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippet extends TemplatedSnippet {

    private final RestDocumentationContextPlaceholderResolverFactory placeholderResolverFactory =
            new RestDocumentationContextPlaceholderResolverFactory();

    private final PropertyPlaceholderHelper propertyPlaceholderHelper =
            new PropertyPlaceholderHelper("{", "}");

    public SectionSnippet() {
        super("section", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        HandlerMethod handlerMethod = getHandlerMethod(operation);
        final String title;
        if (handlerMethod != null) {
            title = join(splitByCharacterTypeCamelCase(
                    capitalize(handlerMethod.getMethod().getName())), ' ');
        } else {
            title = "";
        }

        // resolve path
        String path = propertyPlaceholderHelper.replacePlaceholders(operation.getName(),
                placeholderResolverFactory.create(getDocumentationContext(operation)));

        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("link", delimit(path));
        model.put("path", path);
        return model;
    }

    private String delimit(String value) {
        return value.replace("/", "-");
    }
}