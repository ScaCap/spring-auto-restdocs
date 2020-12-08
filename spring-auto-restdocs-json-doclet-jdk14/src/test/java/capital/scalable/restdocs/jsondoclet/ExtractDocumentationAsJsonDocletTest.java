/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK14+
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class ExtractDocumentationAsJsonDocletTest {

    private static final Path TGT_PATH = Path.of("target/test/generated-javadoc-json").toAbsolutePath();

    private static final List<String> args = List.of(
        "--enable-preview",
        "--release", "14",
        "-private",
        "-d", TGT_PATH.toString()
    );

    @Before
    public void setup() throws IOException {
        Files.createDirectories(TGT_PATH);
    }

    @Test
    public void testDocumentedClass() throws IOException, JSONException {
        var src = new InMemoryJavaFileObject(URI.create("string:///DocumentedClass.java"),
            """
            import java.math.BigDecimal;
            
            /**
             * This is a test class. UTF 8 test: 我能吞下玻璃而不伤身体。Árvíztűrő tükörfúrógép
             *
             * @ Don't fail on invalid tags
             */
            class DocumentedClass {
                /**
                 * Location of resource
                 *
                 * @see path
                 */
                public String location;
            
                /**
                 * Path within location
                 *
                 * @ Don't fail on invalid tags
                 */
                private BigDecimal path;
            
                Boolean notDocumented;
            
                /**
                 * Executes request on specified location
                 *
                 * @title Execute request
                 */
                public void execute() {
                }
            
                /**
                 * Initiates request (テスト)
                 *
                 * @param when  when to initiate (テスト)
                 * @param force true if force (テスト)
                 * @title Do initiate (テスト)
                 * @ Don't fail on invalid tags
                 * @deprecated use other method (テスト)
                 */
                private void initiate(String when, boolean force, int notDocumented) {
                }
            
                void notDocumented() {
                }
            }
            """
        );

        var task = ToolProvider.getSystemDocumentationTool().getTask(null, null, null, ExtractDocumentationAsJsonDoclet.class, args, List.of(src));

        boolean result = task.call();
        assertTrue(result);

        var expected =
            """
            {
              "comment": "This is a test class. UTF 8 test: 我能吞下玻璃而不伤身体。Árvíztűrő tükörfúrógép",
              "fields": {
                "path": {
                  "comment": "Path within location",
                  "tags": {}
                },
                "location": {
                  "comment": "Location of resource",
                  "tags": {
                    "see": "path"
                  }
                },
                "notDocumented": {
                  "comment": "",
                  "tags": {}
                }
              },
              "methods": {
                "initiate": {
                  "comment": "Initiates request (テスト)",
                  "parameters": {
                    "force": "true if force (テスト)",
                    "when": "when to initiate (テスト)"
                  },
                  "tags": {
                    "deprecated": "use other method (テスト)",
                    "title": "Do initiate (テスト)"
                  }
                },
                "execute": {
                  "comment": "Executes request on specified location",
                  "parameters": {},
                  "tags": {
                    "title": "Execute request"
                  }
                },
                "notDocumented": {
                  "comment": "",
                  "parameters": {},
                  "tags": {}
                }
              }
            }
            """;

        String generated = IOUtils.toString(new FileInputStream(TGT_PATH.resolve("DocumentedClass.json").toFile()), UTF_8);
        JSONAssert.assertEquals(expected, generated, false);

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
