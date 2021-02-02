/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.SnippetException;

public class FieldDescriptorUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void assertAllDocumented() throws Exception {
        List<FieldDescriptor> fields = new ArrayList<>();
        fields.add(fieldWithPath("1").description(""));
        fields.add(fieldWithPath("2").description("something"));
        fields.add(fieldWithPath("2"));

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following fields were not documented: [1, 2]");

        FieldDescriptorUtil.assertAllDocumented(fields, "fields");
    }
}
