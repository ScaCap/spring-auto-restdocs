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

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.Parameters;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MultipartContentOperationPreprocessorTest {
    private static final byte[] EXPECTED_CONTENT = "<binary>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] BINARY_CONTENT = new byte[]{1, 2, 3, 3, 3};
    private MultipartContentOperationPreprocessor preprocessor;
    private OperationRequestFactory requestFactory;
    private OperationRequestPartFactory partFactory;

    @Before
    public void setUp() {
        preprocessor = new MultipartContentOperationPreprocessor();
        requestFactory = new OperationRequestFactory();
        partFactory = new OperationRequestPartFactory();
    }

    @Test
    public void shouldReplaceContent() throws URISyntaxException {
        OperationRequest request = createRequestWithContentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        OperationRequest processedRequest = preprocessor.preprocess(request);
        assertArrayEquals(request.getContent(), processedRequest.getContent());
        assertEquals(request.getHeaders(), processedRequest.getHeaders());
        assertEquals(request.getMethod(), processedRequest.getMethod());
        assertEquals(request.getUri(), processedRequest.getUri());
        assertEquals(request.getParameters(), processedRequest.getParameters());
        for (OperationRequestPart part : processedRequest.getParts()) {
            assertArrayEquals(EXPECTED_CONTENT, part.getContent());
        }
    }

    @Test
    public void shouldNotReplaceWhenNotMultipart() throws URISyntaxException {
        OperationRequest request = createRequestWithContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        OperationRequest processedRequest = preprocessor.preprocess(request);
        assertArrayEquals(request.getContent(), processedRequest.getContent());
        assertEquals(request.getHeaders(), processedRequest.getHeaders());
        assertEquals(request.getMethod(), processedRequest.getMethod());
        assertEquals(request.getUri(), processedRequest.getUri());
        assertEquals(request.getParameters(), processedRequest.getParameters());

        OperationRequestPart[] expectedParts = request.getParts().toArray(new OperationRequestPart[0]);
        OperationRequestPart[] operationRequestParts = processedRequest.getParts().toArray(new OperationRequestPart[0]);
        for (int i = 0; i < operationRequestParts.length; i++) {
            assertEquals(expectedParts[i], operationRequestParts[i]);
        }
    }

    private OperationRequest createRequestWithContentType(String contentType) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        Parameters parameters = new Parameters();
        parameters.add("parameter", "value");
        List<OperationRequestPart> requestParts = new ArrayList<>();
        requestParts.add(partFactory.create("first", "file1", BINARY_CONTENT, new HttpHeaders()));
        return requestFactory.create(new URI("http://localhost"), HttpMethod.POST, BINARY_CONTENT, headers, parameters, requestParts);
    }
}
