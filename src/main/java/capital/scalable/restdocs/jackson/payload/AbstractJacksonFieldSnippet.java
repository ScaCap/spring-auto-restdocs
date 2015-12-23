package capital.scalable.restdocs.jackson.payload;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;

import capital.scalable.restdocs.jackson.jackson.FieldDocumentationGenerator;

abstract class AbstractJacksonFieldSnippet extends TemplatedSnippet {

    protected AbstractJacksonFieldSnippet(String type) {
        this(type, null);
    }

    protected AbstractJacksonFieldSnippet(String type, Map<String, Object> attributes) {
        super(type + "-fields", attributes);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        MvcResult result = (MvcResult) operation.getAttributes().get(MvcResult.class.getName());
        ObjectMapper objectMapper =
                (ObjectMapper) operation.getAttributes().get(ObjectMapper.class.getName());

        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        Object handler = result.getHandler();
        if (handler != null && handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            Type type = getType(method);
            if (type != null) {
                try {
                    fieldDescriptors.addAll(new FieldDocumentationGenerator(objectMapper.writer())
                            .generateDocumentation(type, objectMapper.getTypeFactory()));
                } catch (JsonMappingException e) {
                    throw new JacksonFieldProcessingException("Error while parsing fields", e);
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        enrichModel(result, model);

        List<Map<String, Object>> fields = new ArrayList<>();
        model.put("fields", fields);
        for (FieldDescriptor descriptor : fieldDescriptors) {
            fields.add(createModelForDescriptor(descriptor));
        }
        model.put("hasFields", !fieldDescriptors.isEmpty());
        model.put("noFields", fieldDescriptors.isEmpty());
        return model;
    }

    /**
     * Returns a model for the given {@code descriptor}
     *
     * @param descriptor the descriptor
     * @return the model
     */
    protected Map<String, Object> createModelForDescriptor(FieldDescriptor descriptor) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("path", descriptor.getPath());
        model.put("type", descriptor.getType().toString());
        model.put("optional", descriptor.isOptional());
        model.put("description", descriptor.getDescription().toString());
        return model;
    }

    protected Type firstGenericType(MethodParameter param) {
        return ((ParameterizedType) param.getGenericParameterType()).getActualTypeArguments()[0];
    }

    protected abstract Type getType(HandlerMethod method);

    protected void enrichModel(MvcResult result, Map<String, Object> model) {
        // can be used to add additional fields
    }
}
