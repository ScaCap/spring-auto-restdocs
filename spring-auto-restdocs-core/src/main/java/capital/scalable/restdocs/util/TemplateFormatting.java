/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.util;

public class TemplateFormatting {
    private static final String LINE_BREAK_ADOC = " +\n";
    private static final String LINE_BREAK_MD = "<br>";
    private static final String BOLD_ADOC = "**";
    private static final String BOLD_MD = "**";
    private static final String ITALICS_ADOC = "__";
    private static final String ITALICS_MD = "*";
    private static final String CODE_ADOC = "`";
    private static final String CODE_MD = "`";
    private static final String LINK_ADOC = "link:$1[$2]";
    private static final String LINK_MD = "[$2]($1)";

    public static TemplateFormatting ASCIIDOC =
            new TemplateFormatting(LINE_BREAK_ADOC, BOLD_ADOC, ITALICS_ADOC, CODE_ADOC, LINK_ADOC);

    public static TemplateFormatting MARKDOWN =
            new TemplateFormatting(LINE_BREAK_MD, BOLD_MD, ITALICS_MD, CODE_MD, LINK_MD);

    private final String lineBreak;
    private final String bold;
    private final String italics;
    private final String code;
    private final String link;

    private TemplateFormatting(String lineBreak, String bold, String italics, String code, String link) {
        this.lineBreak = lineBreak;
        this.bold = bold;
        this.italics = italics;
        this.code = code;
        this.link = link;
    }

    public String getLineBreak() {
        return lineBreak;
    }

    public String getBold() {
        return bold;
    }

    public String getItalics() {
        return italics;
    }

    public String getCode() {
        return code;
    }

    public String link() {
        return link;
    }
}
