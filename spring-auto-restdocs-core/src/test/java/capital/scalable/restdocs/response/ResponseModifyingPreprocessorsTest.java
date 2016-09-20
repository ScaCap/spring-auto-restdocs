package capital.scalable.restdocs.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

public class ResponseModifyingPreprocessorsTest {

    @Test
    public void shouldShortenJsonArraysWithCombinedShortener() {
        // given
        ObjectMapper mapper = new ObjectMapper();
        OperationResponse unprocessedResponse =
                new OperationResponseFake("[1,2,3,4]", APPLICATION_JSON_VALUE);
        // when
        OperationResponsePreprocessor preprocessor =
                ResponseModifyingPreprocessors.shortenContent(mapper);
        OperationResponse processedResponse = preprocessor.preprocess(unprocessedResponse);
        // then
        assertThat(processedResponse.getContentAsString(), is("[1,2,3]"));
    }

    @Test
    public void shouldShortenBinaryContentWithCombinedShortener() {
        // given
        ObjectMapper mapper = new ObjectMapper();
        OperationResponse unprocessedResponse =
                new OperationResponseFake("does not matter", IMAGE_JPEG_VALUE);
        // when
        OperationResponsePreprocessor preprocessor =
                ResponseModifyingPreprocessors.shortenContent(mapper);
        OperationResponse processedResponse = preprocessor.preprocess(unprocessedResponse);
        // then
        assertThat(processedResponse.getContentAsString(), is("<binary>"));
    }

    @Test
    public void shouldShortenJsonArrays() {
        // given
        ObjectMapper mapper = new ObjectMapper();
        OperationResponse unprocessedResponse =
                new OperationResponseFake("[1,2,3,4]", APPLICATION_JSON_VALUE);
        // when
        OperationPreprocessor preprocessor =
                ResponseModifyingPreprocessors.limitJsonArrayLength(mapper);
        OperationResponse processedResponse = preprocessor.preprocess(unprocessedResponse);
        // then
        assertThat(processedResponse.getContentAsString(), is("[1,2,3]"));
    }

    @Test
    public void shouldShortenBinary() {
        // given
        OperationResponse unprocessedResponse =
                new OperationResponseFake("does not matter", IMAGE_JPEG_VALUE);
        // when
        OperationPreprocessor preprocessor =
                ResponseModifyingPreprocessors.replaceBinaryContent();
        OperationResponse processedResponse = preprocessor.preprocess(unprocessedResponse);
        // then
        assertThat(processedResponse.getContentAsString(), is("<binary>"));
    }

    private static class OperationResponseFake implements OperationResponse {

        private final String content;

        private final HttpHeaders httpHeaders;

        public OperationResponseFake(String content, String contentType) {
            this.content = content;
            this.httpHeaders = new HttpHeaders();
            this.httpHeaders.put(HttpHeaders.CONTENT_TYPE, singletonList(contentType));
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.OK;
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
