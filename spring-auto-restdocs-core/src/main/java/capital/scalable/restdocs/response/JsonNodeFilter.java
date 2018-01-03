package capital.scalable.restdocs.response;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonNodeFilter {

    boolean apply(JsonNode node);
}
