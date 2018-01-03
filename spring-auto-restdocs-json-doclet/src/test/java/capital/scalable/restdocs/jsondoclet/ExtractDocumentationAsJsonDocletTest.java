package capital.scalable.restdocs.jsondoclet;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class ExtractDocumentationAsJsonDocletTest {

    private static final String JSON_PATH =
            "capital/scalable/restdocs/jsondoclet/DocumentedClass.json";

    /**
     * The test requires that the Doclet is executed before. This is ensured by
     * the Maven configuration, but not when the test is executed on its own.
     */
    @Test
    public void testDocumentedClass() throws IOException, JSONException {
        String generated = IOUtils.toString(
                new FileInputStream(new File("target/generated-javadoc-json/" + JSON_PATH)), UTF_8);
        String expected = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream(JSON_PATH), UTF_8);
        JSONAssert.assertEquals(expected, generated, false);
    }
}
