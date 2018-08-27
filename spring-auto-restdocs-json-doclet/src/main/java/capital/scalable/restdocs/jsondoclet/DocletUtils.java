/*-
 * #%L
 * Spring Auto REST Docs Json Doclet
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
package capital.scalable.restdocs.jsondoclet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javadoc.Tag;

public class DocletUtils {
    private DocletUtils() {
        // utils
    }

    private static String cleanupTagName(String name) {
        return name.startsWith("@") ? name.substring(1) : name;
    }

    public static Map<String, List<String>> extractTags(Tag[] tags) {
        Map<String, List<String>> map = new HashMap<>();
        for (Tag tag : tags) {
            mergeTag(map, tag);
        }
        return map;
    }

    public static void mergeTag(Map<String, List<String>> map, Tag tag) {
        String key = cleanupTagName(tag.name());
        List<String> values = map.get(key);
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(tag.text().trim());
        map.put(key, values);
    }
}
