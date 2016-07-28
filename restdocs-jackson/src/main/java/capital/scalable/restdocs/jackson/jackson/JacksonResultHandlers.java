package capital.scalable.restdocs.jackson.jackson;

import static capital.scalable.restdocs.jackson.OperationAttributeHelper.initRequestPattern;
import static capital.scalable.restdocs.jackson.OperationAttributeHelper.setConstraintReader;
import static capital.scalable.restdocs.jackson.OperationAttributeHelper.setHandlerMethod;
import static capital.scalable.restdocs.jackson.OperationAttributeHelper.setJavadocReader;
import static capital.scalable.restdocs.jackson.OperationAttributeHelper.setObjectMapper;

import capital.scalable.restdocs.jackson.constraints.ConstraintReaderImpl;
import capital.scalable.restdocs.jackson.javadoc.JavadocReaderImpl;
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
            setHandlerMethod(result.getRequest(), (HandlerMethod) result.getHandler());
            setObjectMapper(result.getRequest(), objectMapper);
            initRequestPattern(result.getRequest());
            setJavadocReader(result.getRequest(), new JavadocReaderImpl());
            setConstraintReader(result.getRequest(), new ConstraintReaderImpl());
        }
    }
}
