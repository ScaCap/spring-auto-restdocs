package capital.scalable.restdocs.jackson.response;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonNodeConsumer {

    void accept(JsonNode node);
}
