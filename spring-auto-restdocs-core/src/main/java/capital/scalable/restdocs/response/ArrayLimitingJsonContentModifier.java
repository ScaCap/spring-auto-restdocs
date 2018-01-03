package capital.scalable.restdocs.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ArrayLimitingJsonContentModifier extends JsonContentModifier {

    protected ArrayLimitingJsonContentModifier(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected void modifyJson(JsonNode root) {
        walk(root, arraysOnly(), shortenArray(3));
    }

    private JsonNodeFilter arraysOnly() {
        return new JsonNodeFilter() {
            @Override
            public boolean apply(JsonNode node) {
                return node.isArray();
            }
        };
    }

    private JsonNodeConsumer shortenArray(final int maxElements) {
        return new JsonNodeConsumer() {
            @Override
            public void accept(JsonNode node) {
                final int originalSize = node.size();
                for (int i = maxElements; i < originalSize; i++) {
                    ((ArrayNode) node).remove(maxElements);
                }
            }
        };
    }
}
