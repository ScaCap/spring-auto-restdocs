package capital.scalable.restdocs.jackson;

import static capital.scalable.restdocs.OperationAttributeHelper.initRequestPattern;
import static capital.scalable.restdocs.OperationAttributeHelper.setConstraintReader;
import static capital.scalable.restdocs.OperationAttributeHelper.setHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.setJavadocReader;
import static capital.scalable.restdocs.OperationAttributeHelper.setObjectMapper;

import capital.scalable.restdocs.constraints.ConstraintReaderImpl;
import capital.scalable.restdocs.javadoc.JavadocReaderImpl;
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
            // HandlerMethod is not present in case of invalid endpoint
            // or in case of static resource url
            if (result.getHandler() instanceof HandlerMethod) {
                setHandlerMethod(result.getRequest(), (HandlerMethod) result.getHandler());
            }
            setObjectMapper(result.getRequest(), objectMapper);
            initRequestPattern(result.getRequest());
            setJavadocReader(result.getRequest(), JavadocReaderImpl.createWithSystemProperty());
            setConstraintReader(result.getRequest(), ConstraintReaderImpl.create());
        }
    }
}
