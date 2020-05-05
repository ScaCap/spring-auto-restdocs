/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DocletUtils {
    /** Map of code replacement pattern to the AsciiDoc markup that surrounds a code snippet/block. */
    private static final Map<Pattern, String> CODE_PATTERNS;
    private static final Pattern VALUE_CLEANUP_PATTERN = Pattern.compile("\\s*@[^\\s]+\\s+");
    private static final Pattern DOC_CLEANUP_PATTERN = Pattern.compile("[\\r\\n]+\\s*@.*");

    static {
        // Order matters to implementation
        final Map<Pattern, String> codePatterns = new LinkedHashMap<>();
        codePatterns.put(Pattern.compile("<pre>\\s*\\{@code(\\s+[^}]+)}\\s*</pre>", Pattern.DOTALL), "....");
        codePatterns.put(Pattern.compile("<code>(.*)</code>"), "`");
        codePatterns.put(Pattern.compile(",?\\{@code\\s+([^}]+)},?"), "`");
        CODE_PATTERNS = Collections.unmodifiableMap(codePatterns);
    }

    private DocletUtils() {
        // utils
    }

    static String cleanupDocComment(String comment) {
        if (comment == null) return "";
        return DOC_CLEANUP_PATTERN.matcher(formatCode(comment)).replaceAll("").trim();
    }

    public static String cleanupTagValue(String value) {
        return VALUE_CLEANUP_PATTERN.matcher(formatCode(value)).replaceFirst("").trim();
    }

    public static String cleanupTagName(String name) {
        return name.startsWith("@") ? name.substring(1) : name;
    }

    public static String formatCode(String textWithCode) {
        String text = textWithCode;
        for (Pattern p : CODE_PATTERNS.keySet()) {
            final String wrapper = CODE_PATTERNS.get(p);
            text = p.matcher(text).replaceAll(mr -> wrapper + mr.group(1) + wrapper);
        }
        return text;
    }

}
