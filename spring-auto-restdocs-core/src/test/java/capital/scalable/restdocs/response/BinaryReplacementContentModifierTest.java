package capital.scalable.restdocs.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.MediaType;

public class BinaryReplacementContentModifierTest {

    @Test
    public void testModifyContentWithPng() {
        testModifyContent(MediaType.IMAGE_PNG);
    }

    @Test
    public void testModifyContentAllTypes() {
        for (MediaType type : BinaryReplacementContentModifier.BINARY_ENUM_TYPES) {
            testModifyContent(type);
        }
    }

    public void testModifyContent(MediaType type) {
        BinaryReplacementContentModifier modifier = new BinaryReplacementContentModifier();
        byte[] result = modifier.modifyContent(new byte[10], type);
        assertThat(result, is("<binary>".getBytes(UTF_8)));
    }
}
