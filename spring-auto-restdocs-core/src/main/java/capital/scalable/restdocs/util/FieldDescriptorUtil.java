/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.SnippetException;

public class FieldDescriptorUtil {
    private FieldDescriptorUtil() {
        // util
    }

    public static void assertAllDocumented(Collection<FieldDescriptor> fieldDescriptors,
            String what) {
        List<String> undocumentedFields = new ArrayList<>();
        for (FieldDescriptor descriptor : fieldDescriptors) {
            if (isBlank((String) descriptor.getDescription())) {
                undocumentedFields.add(descriptor.getPath());
            }
        }
        if (!undocumentedFields.isEmpty()) {
            throw new SnippetException(
                    "Following " + what + " were not documented: " + undocumentedFields);
        }
    }
}
