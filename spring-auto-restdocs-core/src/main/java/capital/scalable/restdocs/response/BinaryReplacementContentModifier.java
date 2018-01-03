package capital.scalable.restdocs.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.IMAGE_GIF;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.ContentModifier;

public class BinaryReplacementContentModifier implements ContentModifier {

    public static final Set<MediaType> BINARY_ENUM_TYPES = new HashSet<>();

    static {
        BINARY_ENUM_TYPES.add(MediaType.valueOf("application/pdf"));
        BINARY_ENUM_TYPES.add(APPLICATION_OCTET_STREAM);
        BINARY_ENUM_TYPES.add(IMAGE_JPEG);
        BINARY_ENUM_TYPES.add(IMAGE_GIF);
        BINARY_ENUM_TYPES.add(IMAGE_PNG);
    }

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
