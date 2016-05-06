package capital.scalable.restdocs.jackson;

import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

import java.util.Map;

import capital.scalable.restdocs.jackson.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletRequest;
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
}
