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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessorAdapter;

public class MultipartContentOperationPreprocessor extends OperationPreprocessorAdapter {
    private static final byte[] BINARY_REPLACEMENT = "<binary>".getBytes(UTF_8);

    private final OperationRequestPartFactory partFactory = new OperationRequestPartFactory();
    private final OperationRequestFactory requestFactory = new OperationRequestFactory();

    @Override
    public OperationRequest preprocess(OperationRequest request) {
        if (isMultipart(request)) {
            List<OperationRequestPart> parts = request.getParts().stream()
                    .map(this::replaceBinary)
                    .collect(toList());
            return requestFactory.create(request.getUri(),
                    request.getMethod(), request.getContent(), request.getHeaders(), request.getParameters()
                    , parts);
        }
        return request;
    }

    private boolean isMultipart(OperationRequest request) {
        List<String> contentTypes = request.getHeaders().get(HttpHeaders.CONTENT_TYPE);
        return contentTypes != null && contentTypes.contains(MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    private OperationRequestPart replaceBinary(OperationRequestPart part) {
        return partFactory.create(part.getName(), part.getSubmittedFileName()
                , BINARY_REPLACEMENT, part.getHeaders());
    }
}
