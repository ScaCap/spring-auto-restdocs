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
