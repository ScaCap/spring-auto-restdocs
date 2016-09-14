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
                "target/generated-javadoc-json/capital/scalable/restdocs/jsondoclet" +
                        "/DocumentedClass.json")));
        String expected = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(
                "capital/scalable/restdocs/jsondoclet/DocumentedClass.json"));
        assertThat(expected, equalTo(generated));
    }
}
