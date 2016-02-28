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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.restdocs.operation.preprocess.ContentModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

/**
 * @author Juraj Misur
 */
public class ResponseModifyingPreprocessors {

    /**
     * Only modifies the content of a response.
     * <p>
     * Binary: Replaces content with "<binary>"
     * <p>
     * JSON: Limits the length of all JSON arrays in the response to 3 elements.
     *
     * @param objectMapper
     * @return
     */
    public static OperationResponsePreprocessor shortenContent(ObjectMapper objectMapper) {
        return Preprocessors.preprocessResponse(replaceBinaryContent(),
                limitJsonArrayLength(objectMapper));
    }

    /**
     * Modifies request and response.
     *
     * @return
     */
    public static OperationPreprocessor replaceBinaryContent() {
        return new ContentModifyingOperationPreprocessor(new BinaryReplacementContentModifier());
    }

    /**
     * Modifies request and response.
     *
     * @return
     */
    public static OperationPreprocessor limitJsonArrayLength(ObjectMapper objectMapper) {
        return new ContentModifyingOperationPreprocessor(
                new ArrayLimitingJsonContentModifier(objectMapper));
    }
}
