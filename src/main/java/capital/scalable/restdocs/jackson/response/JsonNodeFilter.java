package capital.scalable.restdocs.jackson.response;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonNodeFilter {

    boolean apply(JsonNode node);
}
