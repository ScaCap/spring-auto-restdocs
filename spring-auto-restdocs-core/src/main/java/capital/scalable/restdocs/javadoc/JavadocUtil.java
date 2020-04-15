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
package capital.scalable.restdocs.javadoc;

import capital.scalable.restdocs.util.TemplateFormatting;

public class JavadocUtil {
    private static String FORCED_LB = "__FLB__";
    private static String LB = "__LB__";

    private JavadocUtil() {
        // util
    }

    public static String convertFromJavadoc(String javadoc, TemplateFormatting templateFormatting) {
        String converted = javadoc
                // line breaks in javadoc are ignored
                .replace("\n", "")
                // will be replaced with forced line break
                .replace("<br/>", FORCED_LB)
                .replace("<br>", FORCED_LB)
                // will be replaced with normal line break
                .replace("<p>", LB + LB)
                .replace("</p>", LB + LB)
                .replace("<ul>", LB)
                .replace("</ul>", LB + LB)
                .replace("<li>", LB + "- ")
                .replace("</li>", "")
                .replace("<b>", templateFormatting.getBold())
                .replace("</b>", templateFormatting.getBold())
                .replace("<strong>", templateFormatting.getBold())
                .replace("</strong>", templateFormatting.getBold())
                .replace("<i>", templateFormatting.getItalics())
                .replace("</i>", templateFormatting.getItalics())
                .replace("<em>", templateFormatting.getItalics())
                .replace("</em>", templateFormatting.getItalics())
                .replace("<code>", templateFormatting.getCode())
                .replace("</code>", templateFormatting.getCode())
                .replaceAll("<a\\s+href\\s*=\\s*[\"\'](.*?)[\"\']\\s*>(.*?)</a>",
                        templateFormatting.link());

        if (converted.isEmpty()) {
            return "";
        }

        String res = trimAndFixLineBreak(converted, FORCED_LB, templateFormatting.getLineBreak());
        res = trimAndFixLineBreak(res, LB, "\n");
        return res;
    }

    private static String trimAndFixLineBreak(String text, String separator, String newSeparator) {
        StringBuilder res = new StringBuilder();
        for (String line : text.split(separator)) {
            res.append(line.trim()).append(newSeparator);
        }
        res.delete(res.lastIndexOf(newSeparator), res.length());
        return res.toString();
    }
}
