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

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

public class RecordTest {

    private static final Path TGT_PATH = Path.of("target/test/generated-javadoc-json").toAbsolutePath();

    private static final List<String> args = List.of(
        "--enable-preview",
        "--release", Integer.toString(Runtime.version().feature()),
        "-private",
        "-d", TGT_PATH.toString()
    );

    @Before
    public void setup() throws IOException {
        Files.createDirectories(TGT_PATH);
    }

    @Test
    public void testDocumentedRecord() throws IOException {
        var src = new InMemoryJavaFileObject(URI.create("string:///DocumentedRecord.java"),
            """
            /**
             * This is a test record. UTF 8 test: 我能吞下玻璃而不伤身体。Árvíztűrő tükörfúrógép
             *
             * @ Don't fail on invalid tags
             * @param foo this is a record component
             */
            record DocumentedRecord(int foo) {}
            """
        );

        var task = ToolProvider.getSystemDocumentationTool().getTask(null, null, null, ExtractDocumentationAsJsonDoclet.class, args, List.of(src));

        boolean result = task.call();
        assertTrue(result);

        var mapper = new ObjectMapper();
        var tree = mapper.readTree(TGT_PATH.resolve("DocumentedRecord.json").toFile());
        assertEquals("This is a test record. UTF 8 test: 我能吞下玻璃而不伤身体。Árvíztűrő tükörfúrógép", tree.at("/comment").asText());
        assertEquals("this is a record component", tree.at("/fields/foo/comment").asText());

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
