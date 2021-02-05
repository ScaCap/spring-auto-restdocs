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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.ContentModifier;

public abstract class JsonContentModifier implements ContentModifier {

    private static final List<MediaType> supportedMediaTypes = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.valueOf("application/*+json"));
    private final ObjectMapper objectMapper;

    public JsonContentModifier(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] modifyContent(byte[] originalContent, MediaType contentType) {
        if (originalContent.length > 0 && supportedMediaTypes.stream().anyMatch(type -> type.isCompatibleWith(contentType))) {
            return modifyContent(originalContent);
        } else {
            return originalContent;
        }
    }

    /**
     * Uses UTF-8. According to {@link com.fasterxml.jackson.core.JsonEncoding}
     * only UTF-{8,16,32} are a valid JSON encodings.
     *
     * @param originalContent original JSON content
     * @return modified JSON content
     */
    protected byte[] modifyContent(byte[] originalContent) {
        try {
            JsonNode root = objectMapper.readTree(originalContent);
            modifyJson(root);
            return objectMapper.writeValueAsBytes(root);
        } catch (IOException e) {
            throw new JsonModifyingException("Failed to modify JSON response", e);
        }
    }

    protected abstract void modifyJson(JsonNode root);

    protected void walk(JsonNode node, JsonNodeFilter filter,
            JsonNodeConsumer consumer) {
        if (filter.apply(node)) {
            consumer.accept(node);
        }
        for (JsonNode child : node) {
            walk(child, filter, consumer);
        }
    }
}
