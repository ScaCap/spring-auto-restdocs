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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.StringUtils.arrayToDelimitedString;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

import java.util.Collection;

public class FormatUtil {
    private FormatUtil() {
        // util
    }

    public static String arrayToString(Object[] o) {
        return "[" + arrayToDelimitedString(o, ", ") + "]";
    }

    public static String collectionToString(Collection<?> c) {
        return "[" + collectionToDelimitedString(c, ", ") + "]";
    }

    public static String addDot(String text) {
        return isNotBlank(text) && !text.matches("^(?s).*(?:[.?!:;)\"]|</(?:p|ul|ol)>)\\s*$") && !text.endsWith(" ")
                ? text + "."
                : text;
    }

    public static String join(String delimiter, Object... texts) {
        StringBuilder result = new StringBuilder();
        for (Object text : texts) {
            if (text != null && isNotBlank(text.toString())) {
                result.append(text.toString());
                result.append(delimiter);
            }
        }
        if (result.length() > 0) {
            return result.substring(0, result.length() - delimiter.length());
        } else {
            return result.toString();
        }
    }

    public static String fixLineSeparator(String str) {
        return str.replace("\n", System.lineSeparator());
    }
}
