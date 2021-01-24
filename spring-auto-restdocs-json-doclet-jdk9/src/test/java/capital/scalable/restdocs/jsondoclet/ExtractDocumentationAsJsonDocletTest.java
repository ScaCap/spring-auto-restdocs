/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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
package capital.scalable.restdocs.jsondoclet;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.tools.DocumentationTool.DocumentationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import static org.junit.Assert.assertTrue;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class ExtractDocumentationAsJsonDocletTest {

    private static final Path SRC_PATH = FileSystems.getDefault().getPath("src/test/resources").toAbsolutePath();
    private static final Path TGT_PATH = FileSystems.getDefault().getPath("target/test/generated-javadoc-json").toAbsolutePath();

    private static final String JSON_PATH =
            "capital/scalable/restdocs/jsondoclet/DocumentedClass.json";

    private static final List<String> args = List.of(
        "--release", "9",
        "-private",
        "-d", TGT_PATH.toString()
    );

    @Before
    public void setup() throws IOException {
        Files.createDirectories(TGT_PATH);
    }

    @Test
    public void testDocumentedClass() throws IOException, JSONException {
        Path source = SRC_PATH.resolve("capital/scalable/restdocs/jsondoclet/DocumentedClass.java");
        Iterable<JavaFileObject> compilationUnits = compilationUnits(source);

        DocumentationTask task = ToolProvider.getSystemDocumentationTool().getTask(null, null, null, ExtractDocumentationAsJsonDoclet.class, args, compilationUnits);

        boolean result = task.call();
        assertTrue(result);

        try (
            InputStream expectedStream = new FileInputStream(SRC_PATH.resolve(JSON_PATH).toFile());
            InputStream generatedStream = new FileInputStream(TGT_PATH.resolve(JSON_PATH).toFile());
        ) {
            String expected = IOUtils.toString(expectedStream, UTF_8);
            String generated = IOUtils.toString(generatedStream, UTF_8);
            JSONAssert.assertEquals(expected, generated, false);
        }
    }

    Iterable<JavaFileObject> compilationUnits(Path path) throws IOException {
        try (InputStream in = new FileInputStream(path.toFile())) {
            String content = IOUtils.toString(in, UTF_8);
            return List.of(new InMemoryJavaFileObject(path.toUri(), content));
        }
    }

    static class InMemoryJavaFileObject extends SimpleJavaFileObject {

        private final String source;

        public InMemoryJavaFileObject(URI uri, String sourceCode) {
            super(uri, Kind.SOURCE);
            this.source = sourceCode;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }

    }

}
