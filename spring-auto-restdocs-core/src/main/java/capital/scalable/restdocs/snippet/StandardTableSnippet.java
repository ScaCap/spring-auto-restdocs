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

import static capital.scalable.restdocs.OperationAttributeHelper.determineTemplateFormatting;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.DEPRECATED_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static capital.scalable.restdocs.javadoc.JavadocUtil.convertFromJavadoc;
import static capital.scalable.restdocs.util.FormatUtil.addDot;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.util.TemplateFormatting;
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

        Collection<FieldDescriptor> fieldDescriptors =
                createFieldDescriptors(operation, handlerMethod);
        TemplateFormatting templateFormatting = determineTemplateFormatting(operation);
        return createModel(handlerMethod, model, fieldDescriptors, templateFormatting);
    }

    protected abstract Collection<FieldDescriptor> createFieldDescriptors(Operation operation,
            HandlerMethod handlerMethod);

    protected void enrichModel(Map<String, Object> model, HandlerMethod handlerMethod) {
        // can be used to add additional fields
    }

    private Map<String, Object> createModel(HandlerMethod handlerMethod, Map<String, Object> model,
            Collection<FieldDescriptor> fieldDescriptors, TemplateFormatting templateFormatting) {
        List<Map<String, Object>> fields = new ArrayList<>();
        model.put("content", fields);
        for (FieldDescriptor descriptor : fieldDescriptors) {
            fields.add(createModelForDescriptor(descriptor, templateFormatting));
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
            TemplateFormatting templateFormatting) {
        String path = descriptor.getPath();
        String type = toString(descriptor.getType());
        String methodComment = resolveComment(descriptor);
        String deprecated = resolveDeprecated(descriptor);
        String description = convertFromJavadoc(deprecated + methodComment, templateFormatting);

        String optional = resolveOptional(descriptor, templateFormatting);
        List<String> constraints = resolveConstraints(descriptor);
        description = joinAndFormat(description, constraints, templateFormatting);

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

    private String resolveOptional(FieldDescriptor descriptor,
            TemplateFormatting templateFormatting) {
        List<String> optionalMessages = (List<String>) descriptor.getAttributes()
                .get(OPTIONAL_ATTRIBUTE);
        return "" + join(optionalMessages, templateFormatting.getLineBreak());
    }

    private String resolveComment(FieldDescriptor descriptor) {
        return capitalize(addDot(toString(descriptor.getDescription())));
    }

    private String resolveDeprecated(FieldDescriptor descriptor) {
        Object deprecated = descriptor.getAttributes().get(DEPRECATED_ATTRIBUTE);
        if (deprecated != null) {
            return "<b>Deprecated.</b> " + capitalize(addDot(toString(deprecated))) + "<p>";
        } else {
            return "";
        }
    }

    private String toString(Object value) {
        if (value != null) {
            return trimToEmpty(value.toString());
        } else {
            return "";
        }
    }

    private String joinAndFormat(String description, List<String> constraints,
            TemplateFormatting templateFormatting) {
        StringBuilder res = new StringBuilder(description);
        if (!description.isEmpty() && !description.endsWith(".")) {
            res.append('.');
        }

        StringBuilder constr = formatConstraints(constraints, templateFormatting.getLineBreak());
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
