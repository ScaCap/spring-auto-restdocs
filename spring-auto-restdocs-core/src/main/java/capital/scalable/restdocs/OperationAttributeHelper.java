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

package capital.scalable.restdocs;

import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

import java.util.Map;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.misc.AuthorizationSnippet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.operation.Operation;
import org.springframework.web.method.HandlerMethod;

public class OperationAttributeHelper {
    private static final String ATTRIBUTE_NAME_CONFIGURATION =
            "org.springframework.restdocs.configuration";
    public static final String REQUEST_PATTERN = "REQUEST_PATTERN";

    public static HandlerMethod getHandlerMethod(Operation operation) {
        Map<String, Object> attributes = operation.getAttributes();
        return (HandlerMethod) attributes.get(HandlerMethod.class.getName());
    }

    public static void setHandlerMethod(MockHttpServletRequest request,
            HandlerMethod handlerMethod) {
        ((Map) request.getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                .put(HandlerMethod.class.getName(), handlerMethod);
    }

    public static String getRequestPattern(Operation operation) {
        Map<String, Object> attributes = operation.getAttributes();
        return (String) attributes.get(REQUEST_PATTERN);
    }

    public static void initRequestPattern(MockHttpServletRequest request) {
        String requestPattern = (String) request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        ((Map) request.getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                .put(REQUEST_PATTERN, requestPattern);
    }

    public static String getRequestMethod(Operation operation) {
        return operation.getRequest().getMethod().name();
    }

    public static ObjectMapper getObjectMapper(Operation operation) {
        return (ObjectMapper) operation.getAttributes().get(ObjectMapper.class.getName());
    }

    public static void setObjectMapper(MockHttpServletRequest request, ObjectMapper objectMapper) {
        ((Map) request.getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                .put(ObjectMapper.class.getName(), objectMapper);
    }

    public static JavadocReader getJavadocReader(Operation operation) {
        return (JavadocReader) operation.getAttributes().get(JavadocReader.class.getName());
    }

    public static void setJavadocReader(MockHttpServletRequest request,
            JavadocReader javadocReader) {
        ((Map) request.getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                .put(JavadocReader.class.getName(), javadocReader);
    }

    public static RestDocumentationContext getDocumentationContext(Operation operation) {
        return (RestDocumentationContext) operation
                .getAttributes().get(RestDocumentationContext.class.getName());
    }

    public static String getAuthorization(Operation operation) {
        return (String) operation.getAttributes().get(AuthorizationSnippet.class.getName());
    }

    public static void setAuthorization(MockHttpServletRequest request,
            String authorization) {
        ((Map) request.getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                .put(AuthorizationSnippet.class.getName(), authorization);
    }

    public static ConstraintReader getConstraintReader(Operation operation) {
        return (ConstraintReader) operation.getAttributes().get(ConstraintReader.class.getName());
    }

    public static void setConstraintReader(MockHttpServletRequest request,
            ConstraintReader constraintReader) {
        ((Map) request.getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                .put(ConstraintReader.class.getName(), constraintReader);
    }

}
