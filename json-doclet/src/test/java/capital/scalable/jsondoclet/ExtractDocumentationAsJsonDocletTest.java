package capital.scalable.jsondoclet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ExtractDocumentationAsJsonDocletTest {

    @Test
    public void testDocumentedClass() throws IOException {
        String generated = IOUtils.toString(new FileInputStream(new File(
                "target/generated-javadoc-json/capital.scalable.jsondoclet.DocumentedClass.json")));
        String expected = IOUtils.toString(
                this.getClass().getClassLoader()
                        .getResourceAsStream("capital/scalable/jsondoclet/DocumentedClass.json"));
        assertThat(expected, equalTo(generated));
    }

}
