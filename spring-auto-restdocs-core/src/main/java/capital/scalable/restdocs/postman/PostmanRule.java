package capital.scalable.restdocs.postman;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.rules.ExternalResource;

public class PostmanRule extends ExternalResource {
    private final PostmanCollection postmanCollection;

    public PostmanRule(String name, String description) {
        this.postmanCollection = new PostmanCollection(name, description);
    }

    public PostmanCollection get() {
        return postmanCollection;
    }

    @Override
    protected void after() {
        ObjectMapper objectMapper = setupMapper();
        String json = null;
        try {
            json = objectMapper.writerFor(PostmanCollection.class).writeValueAsString(postmanCollection);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

    private ObjectMapper setupMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibilityChecker(
                objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(ANY)
                        .withGetterVisibility(NONE)
                        .withSetterVisibility(NONE)
                        .withCreatorVisibility(NONE));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }
}