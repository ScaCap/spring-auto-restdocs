package capital.scalable.restdocs.jackson.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.restdocs.operation.preprocess.ContentModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

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
