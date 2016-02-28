/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package capital.scalable.restdocs.jackson.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.MediaType;

public class BinaryReplacementContentModifierTest {

    @Test
    public void testModifyContentWithPng() {
        testModifyContent(MediaType.IMAGE_PNG);
    }

    @Test
    public void testModifyContentAllTypes() {
        for (MediaType type : BinaryReplacementContentModifier.BINARY_ENUM_TYPES) {
            testModifyContent(type);
        }
    }

    public void testModifyContent(MediaType type) {
        BinaryReplacementContentModifier modifier = new BinaryReplacementContentModifier();
        byte[] result = modifier.modifyContent(new byte[10], type);
        assertThat(result, is("<binary>".getBytes(UTF_8)));
    }
}
