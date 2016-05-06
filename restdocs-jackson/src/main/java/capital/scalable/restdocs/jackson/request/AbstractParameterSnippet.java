package capital.scalable.restdocs.jackson.request;

import static capital.scalable.restdocs.jackson.OperationAttributeHelper.getJavadocReader;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.util.StringUtils.hasLength;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.jackson.javadoc.JavadocReader;
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
        JavadocReader javadocReader = getJavadocReader(operation);

        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        for (MethodParameter param : handlerMethod.getMethodParameters()) {
            A annot = getAnnotation(param);
            if (annot != null) {
                addFieldDescriptor(handlerMethod, javadocReader, fieldDescriptors, param, annot);
            }
        }
        return fieldDescriptors;
    }

    private void addFieldDescriptor(HandlerMethod handlerMethod,
            JavadocReader javadocReader, List<FieldDescriptor> fieldDescriptors,
            MethodParameter param, A annot) {

        String javaParameterName = param.getParameterName();
        String pathName = getPath(annot);

        String parameterName = hasLength(pathName) ? pathName : javaParameterName;
        String parameterType = param.getParameterType().getSimpleName();
        String description = javadocReader.resolveMethodParameterComment(
                handlerMethod.getBeanType(), handlerMethod.getMethod().getName(),
                javaParameterName);

        FieldDescriptor descriptor = fieldWithPath(parameterName)
                .type(parameterType)
                .description(description);

        if (!isRequired(annot)) {
            descriptor = descriptor.optional();
        }

        fieldDescriptors.add(descriptor);
    }

    protected abstract boolean isRequired(A annot);

    protected abstract String getPath(A annot);

    abstract A getAnnotation(MethodParameter param);
}
