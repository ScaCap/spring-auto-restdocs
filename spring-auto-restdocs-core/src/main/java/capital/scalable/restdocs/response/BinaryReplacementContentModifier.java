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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.IMAGE_GIF;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.ContentModifier;

public class BinaryReplacementContentModifier implements ContentModifier {

    public static final Set<MediaType> BINARY_ENUM_TYPES = new HashSet<>();

    static {
        BINARY_ENUM_TYPES.add(MediaType.valueOf("application/pdf"));
        BINARY_ENUM_TYPES.add(APPLICATION_OCTET_STREAM);
        BINARY_ENUM_TYPES.add(IMAGE_JPEG);
        BINARY_ENUM_TYPES.add(IMAGE_GIF);
        BINARY_ENUM_TYPES.add(IMAGE_PNG);
    }

    @Override
    public byte[] modifyContent(byte[] originalContent, MediaType contentType) {
        if (originalContent.length > 0 && contentType != null) {
            for (MediaType type : BINARY_ENUM_TYPES) {
                if (contentType.includes(type)) {
                    return replacementText();
                }
            }
        }
        return originalContent;
    }

    public byte[] replacementText() {
        return "<binary>".getBytes(UTF_8);
    }
}
