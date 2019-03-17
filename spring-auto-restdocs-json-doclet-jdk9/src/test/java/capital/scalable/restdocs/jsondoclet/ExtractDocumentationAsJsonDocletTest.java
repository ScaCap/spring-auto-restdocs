/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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
