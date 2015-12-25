package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

public class ExtendedResultHandler implements ResultHandler {

    private final ResultHandler delegate;

    private final ObjectMapper objectMapper;

    public ExtendedResultHandler(ResultHandler delegate, ObjectMapper objectMapper) {
        this.delegate = delegate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(MvcResult result) throws Exception {
        result.getRequest().setAttribute(ObjectMapper.class.getName(), objectMapper);
        result.getRequest().setAttribute(MvcResult.class.getName(), result);
        delegate.handle(result);
    }
}
