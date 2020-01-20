package capital.scalable.restdocs.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessorAdapter;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

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
