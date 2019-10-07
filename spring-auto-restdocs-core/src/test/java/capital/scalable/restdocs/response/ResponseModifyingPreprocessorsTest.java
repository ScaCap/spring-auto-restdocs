/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

public class ResponseModifyingPreprocessorsTest {

    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldShortenJsonArraysWithCombinedShortener() {
        preprocessorTest(new TestData()
                .content("[1,2,3,4]")
                .contentType(APPLICATION_JSON_VALUE)
                .preprocessor(ResponseModifyingPreprocessors.shortenContent(mapper))
                .expectedResult("[1,2,3]"));
    }

    @Test
    public void shouldShortenBinaryContentWithCombinedShortener() {
        preprocessorTest(new TestData()
                .content("does not matter")
                .contentType(IMAGE_PNG_VALUE)
                .preprocessor(ResponseModifyingPreprocessors.shortenContent(mapper))
                .expectedResult("<binary>"));
    }

    @Test
    public void shouldShortenJsonArrays() {
        preprocessorTest(new TestData()
                .content("[1,2,3,4]")
                .contentType(APPLICATION_JSON_VALUE)
                .preprocessor(
                        ResponseModifyingPreprocessors.limitJsonArrayLength(mapper))
                .expectedResult("[1,2,3]"));
    }

    @Test
    public void shouldShortenNestedJsonArrays() {
        preprocessorTest(new TestData()
                .content("{\"x\":{\"y\":[1,2,3,4,5]}}")
                .contentType(APPLICATION_JSON_VALUE)
                .preprocessor(ResponseModifyingPreprocessors.limitJsonArrayLength(mapper))
                .expectedResult("{\"x\":{\"y\":[1,2,3]}}"));
    }

    @Test
    public void shouldShortenJpegAsBinary() {
        preprocessorTest(new TestData()
                .content("does not matter")
                .contentType(IMAGE_JPEG_VALUE)
                .preprocessor(ResponseModifyingPreprocessors.replaceBinaryContent())
                .expectedResult("<binary>"));
    }

    @Test
    public void shouldShortenPdfAsBinary() {
        preprocessorTest(new TestData()
                .content("does not matter")
                .contentType("application/pdf")
                .preprocessor(ResponseModifyingPreprocessors.replaceBinaryContent())
                .expectedResult("<binary>"));
    }

    private void preprocessorTest(TestData testData) {
        // given
        OperationResponse unprocessedResponse =
                new OperationResponseFake(testData.getContent(), testData.getContentType());
        // when
        OperationResponse processedResponse =
                testData.getPreprocessor().preprocess(unprocessedResponse);
        // then
        assertThat(processedResponse.getContentAsString(), is(testData.getExpectedResult()));
    }

    private static class TestData {
        private String content;

        private String contentType;

        private OperationResponsePreprocessor preprocessor;

        private String expectedResult;

        String getContent() {
            return content;
        }

        TestData content(String content) {
            this.content = content;
            return this;
        }

        String getContentType() {
            return contentType;
        }

        TestData contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        OperationResponsePreprocessor getPreprocessor() {
            return preprocessor;
        }

        TestData preprocessor(OperationResponsePreprocessor preprocessor) {
            this.preprocessor = preprocessor;
            return this;
        }

        TestData preprocessor(OperationPreprocessor preprocessor) {
            this.preprocessor = Preprocessors.preprocessResponse(preprocessor);
            return this;
        }

        String getExpectedResult() {
            return expectedResult;
        }

        TestData expectedResult(String expectedResult) {
            this.expectedResult = expectedResult;
            return this;
        }
    }

    private static class OperationResponseFake implements OperationResponse {

        private final String content;

        private final HttpHeaders httpHeaders;

        OperationResponseFake(String content, String contentType) {
            this.content = content;
            this.httpHeaders = new HttpHeaders();
            this.httpHeaders.put(HttpHeaders.CONTENT_TYPE, singletonList(contentType));
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.OK;
        }

        @Override
        public int getStatusCode() {
            return getStatus().value();
        }

        @Override
        public HttpHeaders getHeaders() {
            return httpHeaders;
        }

        @Override
        public byte[] getContent() {
            return content.getBytes(UTF_8);
        }

        @Override
        public String getContentAsString() {
            return content;
        }
    }
}
