package capital.scalable.restdocs.jackson;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.method.HandlerMethod;

public abstract class JacksonResultHandlers {

    static final String ATTRIBUTE_NAME_CONFIGURATION = "org.springframework.restdocs.configuration";

    public static ResultHandler prepareJackson(ObjectMapper objectMapper) {
        return new JacksonPreparingResultHandler(objectMapper);
    }

    private static class JacksonPreparingResultHandler implements ResultHandler {

        private final ObjectMapper objectMapper;

        public JacksonPreparingResultHandler(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void handle(MvcResult result) throws Exception {
            Object handler = result.getHandler();
            if (handler != null && handler instanceof HandlerMethod) {
                ((Map) result.getRequest().getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                        .put(HandlerMethod.class.getName(), handler);
            }
            ((Map) result.getRequest().getAttribute(ATTRIBUTE_NAME_CONFIGURATION))
                    .put(ObjectMapper.class.getName(), objectMapper);
        }
    }
}
