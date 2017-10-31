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

package capital.scalable.restdocs.snippet;

import static capital.scalable.restdocs.OperationAttributeHelper.determineForcedLineBreak;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static capital.scalable.restdocs.javadoc.JavadocUtil.convertFromJavadoc;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

public abstract class StandardTableSnippet extends TemplatedSnippet {

    protected StandardTableSnippet(String snippetName, Map<String, Object> attributes) {
        super(snippetName, attributes);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        HandlerMethod handlerMethod = getHandlerMethod(operation);
        Map<String, Object> model = defaultModel();
        if (handlerMethod == null) {
            return model;
        }

        Collection<FieldDescriptor> fieldDescriptors = emptyList();
        fieldDescriptors = createFieldDescriptors(operation, handlerMethod);

        String forcedLineBreak = determineForcedLineBreak(operation);

        return createModel(handlerMethod, model, fieldDescriptors, forcedLineBreak);
    }

    protected abstract Collection<FieldDescriptor> createFieldDescriptors(Operation operation,
            HandlerMethod handlerMethod);

    protected void enrichModel(Map<String, Object> model, HandlerMethod handlerMethod) {
        // can be used to add additional fields
    }

    private Map<String, Object> createModel(HandlerMethod handlerMethod, Map<String, Object> model,
            Collection<FieldDescriptor> fieldDescriptors, String forcedLineBreak) {
        List<Map<String, Object>> fields = new ArrayList<>();
        model.put("content", fields);
        for (FieldDescriptor descriptor : fieldDescriptors) {
            fields.add(createModelForDescriptor(descriptor, forcedLineBreak));
        }
        model.put("hasContent", !fieldDescriptors.isEmpty());
        model.put("noContent", fieldDescriptors.isEmpty());

        enrichModel(model, handlerMethod);

        return model;
    }

    private Map<String, Object> defaultModel() {
        Map<String, Object> model = new HashMap<>();
        model.put("content", "");
        model.put("hasContent", false);
        model.put("noContent", true);
        return model;
    }

    protected Map<String, Object> createModelForDescriptor(FieldDescriptor descriptor,
            String forcedLineBreak) {
        String path = descriptor.getPath();
        String type = toString(descriptor.getType());
        String description = convertFromJavadoc(toString(descriptor.getDescription()),
                forcedLineBreak);

        String optional = resolveOptional(descriptor, forcedLineBreak);
        List<String> constraints = resolveConstraints(descriptor);
        description = joinAndFormat(description, constraints, forcedLineBreak);

        Map<String, Object> model = new HashMap<>();
        model.put("path", path);
        model.put("type", type);
        model.put("optional", optional);
        model.put("description", description);
        return model;
    }

    private List<String> resolveConstraints(FieldDescriptor descriptor) {
        return (List<String>) descriptor.getAttributes()
                .get(CONSTRAINTS_ATTRIBUTE);
    }

    private String resolveOptional(FieldDescriptor descriptor, String forcedLineBreak) {
        List<String> optionalMessages = (List<String>) descriptor.getAttributes()
                .get(OPTIONAL_ATTRIBUTE);
        return "" + join(optionalMessages, forcedLineBreak);
    }

    private String toString(Object value) {
        if (value != null) {
            return trimToEmpty(value.toString());
        } else {
            return "";
        }
    }

    private String joinAndFormat(String description, List<String> constraints,
            String forcedLineBreak) {
        StringBuilder res = new StringBuilder(description);
        if (!description.isEmpty() && !description.endsWith(".")) {
            res.append('.');
        }

        StringBuilder constr = formatConstraints(constraints, forcedLineBreak);
        if (res.length() > 0 && constr.length() > 0) {
            res.append("\n\n");
        }

        res.append(constr.toString());

        return res.toString().replace("|", "\\|");
    }

    private StringBuilder formatConstraints(List<String> constraints, String forcedLineBreak) {
        StringBuilder res = new StringBuilder();
        for (String constraint : constraints) {
            if (constraint.trim().isEmpty()) {
                continue;
            }
            if (res.length() > 0) {
                res.append(forcedLineBreak);
            }
            res.append(constraint.trim());
            if (!constraint.endsWith(".")) {
                res.append('.');
            }
        }
        return res;
    }
}
