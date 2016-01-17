package capital.scalable.restdocs.jackson.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.IMAGE_GIF;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.ContentModifier;

public class BinaryReplacementContentModifier implements ContentModifier {

    public static final Set<MediaType> BINARY_ENUM_TYPES = ImmutableSet.<MediaType>builder()
            .add(MediaType.valueOf("application/pdf"))
            .add(APPLICATION_OCTET_STREAM)
            .add(IMAGE_JPEG)
            .add(IMAGE_GIF)
            .add(IMAGE_PNG)
            .build();

    @Override
    public byte[] modifyContent(byte[] originalContent, MediaType contentType) {
        if (originalContent.length > 0 && contentType != null) {
            for (MediaType type : BINARY_ENUM_TYPES) {
                if (contentType.includes(type)) {
                    return replacementText();
                }
            }
        }
        return originalContent;
    }

    public byte[] replacementText() {
        return "<binary>".getBytes(UTF_8);
    }
}
