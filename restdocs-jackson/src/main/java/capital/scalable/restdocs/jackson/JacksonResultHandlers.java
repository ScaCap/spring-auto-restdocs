package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.method.HandlerMethod;

public abstract class JacksonResultHandlers {

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
                result.getRequest().setAttribute(HandlerMethod.class.getName(), handler);
            }
            result.getRequest().setAttribute(ObjectMapper.class.getName(), objectMapper);
        }
    }
}
