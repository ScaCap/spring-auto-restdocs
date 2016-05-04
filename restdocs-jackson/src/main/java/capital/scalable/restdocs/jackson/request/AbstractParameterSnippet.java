package capital.scalable.restdocs.jackson.request;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.util.StringUtils.hasLength;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.jackson.javadoc.ClassJavadoc;
import capital.scalable.restdocs.jackson.javadoc.MethodJavadoc;
import capital.scalable.restdocs.jackson.snippet.StandardTableSnippet;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.web.method.HandlerMethod;

abstract class AbstractParameterSnippet<A extends Annotation> extends StandardTableSnippet {
    protected AbstractParameterSnippet(String snippetName, Map<String, Object> attributes) {
        super(snippetName, attributes);
    }

    @Override
    protected List<FieldDescriptor> createFieldDescriptors(Operation operation,
            HandlerMethod handlerMethod) {
        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        for (MethodParameter param : handlerMethod.getMethodParameters()) {
            A annot = getAnnotation(param);
            if (annot != null) {
                addFieldDescriptor(handlerMethod, fieldDescriptors, param, annot);
            }
        }
        return fieldDescriptors;
    }

    private void addFieldDescriptor(HandlerMethod handlerMethod,
            List<FieldDescriptor> fieldDescriptors, MethodParameter param, A annot) {

        String javaName = param.getParameterName();
        String pathName = getPath(annot);

        String parameterName = hasLength(pathName) ? pathName : javaName;
        String parameterType = param.getParameterType().getSimpleName();
        String description = "";
        MethodJavadoc method = resolveMethodJavadoc(handlerMethod);
        if (method != null) {
            description = method.getFields().get(javaName);
        }

        FieldDescriptor descriptor = fieldWithPath(parameterName)
                .type(parameterType)
                .description(description);

        if (!isRequired(annot)) {
            descriptor = descriptor.optional();
        }

        fieldDescriptors.add(descriptor);
    }

    private MethodJavadoc resolveMethodJavadoc(HandlerMethod handlerMethod) {
        ClassJavadoc description = javadocReader
                .loadClass(handlerMethod.getMethod().getDeclaringClass());
        if (description != null) {
            MethodJavadoc method = description.getMethods()
                    .get(handlerMethod.getMethod().getName());
            if (method != null) {
                return method;
            }
        }

        return null;
    }

    protected abstract boolean isRequired(A annot);

    protected abstract String getPath(A annot);

    abstract A getAnnotation(MethodParameter param);
}
