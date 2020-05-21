/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.restdocs.operation.preprocess.ContentModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

public class ResponseModifyingPreprocessors {

    /**
     * Only modifies the content of a response.
     * <p>
     * Contains {@link #replaceBinaryContent()}, {@link #limitJsonArrayLength(ObjectMapper)}
     *
     * @param objectMapper object mapper instance
     * @return a response shortening preprocessor
     */
    public static OperationResponsePreprocessor shortenContent(ObjectMapper objectMapper) {
        return Preprocessors.preprocessResponse(replaceBinaryContent(),
                limitJsonArrayLength(objectMapper));
    }

    /**
     * For binary content, replaces value with "&lt;binary&gt;".
     *
     * @return a preprocessor replacing binary content
     */
    public static OperationPreprocessor replaceBinaryContent() {
        return new ContentModifyingOperationPreprocessor(new BinaryReplacementContentModifier());
    }

    /**
     * For binary content of multipart/form-data requests, replaces value with "&lt;binary&gt;".
     *
     * @return a preprocessor replacing binary content of multipart/form-data requests
     */
    public static OperationPreprocessor replaceMultipartBinaryContent() {
        return new MultipartContentOperationPreprocessor();
    }

    /**
     * For JSON content, cuts the length of all JSON arrays in the response to 3 elements.
     *
     * @param objectMapper object mapper to use
     * @return an array limiting preprocessor
     */
    public static OperationPreprocessor limitJsonArrayLength(ObjectMapper objectMapper) {
        return new ContentModifyingOperationPreprocessor(
                new ArrayLimitingJsonContentModifier(objectMapper));
    }
}
