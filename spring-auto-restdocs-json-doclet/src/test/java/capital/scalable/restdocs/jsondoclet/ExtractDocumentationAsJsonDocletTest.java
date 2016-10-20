/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package capital.scalable.restdocs.jsondoclet;

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

    @Test
    public void testDocumentedClass() throws IOException, JSONException {
        String generated = IOUtils.toString(
                new FileInputStream(new File("target/generated-javadoc-json/" + JSON_PATH)));
        String expected = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream(JSON_PATH));
        JSONAssert.assertEquals(expected, generated, false);
    }
}
