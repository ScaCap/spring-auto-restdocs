/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
        return JsonNode::isArray;
    }

    private JsonNodeConsumer shortenArray(final int maxElements) {
        return node -> {
            final int originalSize = node.size();
            for (int i = maxElements; i < originalSize; i++) {
                ((ArrayNode) node).remove(maxElements);
            }
        };
    }
}
