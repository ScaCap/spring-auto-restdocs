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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FormatUtilTest {
    @Test
    public void arrayToString() {
        assertThat(FormatUtil.arrayToString(new Object[]{"1", 2, "3"})).isEqualTo("[1, 2, 3]");
    }

    @Test
    public void collectionToString() {
        assertThat(FormatUtil.collectionToString(asList("1", 2, "3"))).isEqualTo("[1, 2, 3]");
    }

    @Test
    public void addDot() {
        assertThat(FormatUtil.addDot(null)).isNull();
        assertThat(FormatUtil.addDot("")).isEqualTo("");
        assertThat(FormatUtil.addDot(".")).isEqualTo(".");
        assertThat(FormatUtil.addDot("text.")).isEqualTo("text.");
        assertThat(FormatUtil.addDot("text")).isEqualTo("text.");
        assertThat(FormatUtil.addDot("text?")).isEqualTo("text?");
        assertThat(FormatUtil.addDot("text!")).isEqualTo("text!");
        assertThat(FormatUtil.addDot("text:")).isEqualTo("text:");
        assertThat(FormatUtil.addDot("text;")).isEqualTo("text;");
        assertThat(FormatUtil.addDot("text)")).isEqualTo("text)");
        assertThat(FormatUtil.addDot("text\"")).isEqualTo("text\"");
        assertThat(FormatUtil.addDot("text+")).isEqualTo("text+.");
        assertThat(FormatUtil.addDot("text with html:\n<ul>\n<li>first</li>\n<li>second</li>\n</ul>"))
                .isEqualTo("text with html:\n<ul>\n<li>first</li>\n<li>second</li>\n</ul>");
        assertThat(FormatUtil.addDot("text with html:\r\n<ul>\r\n<li>first</li>\r\n<li>second</li>\r\n</ul>"))
                .isEqualTo("text with html:\r\n<ul>\r\n<li>first</li>\r\n<li>second</li>\r\n</ul>");
        assertThat(FormatUtil.addDot("text with html:\r\n<ol>\r\n<li>first</li>\r\n<li>second</li>\r\n</ol>"))
                .isEqualTo("text with html:\r\n<ol>\r\n<li>first</li>\r\n<li>second</li>\r\n</ol>");
        assertThat(FormatUtil.addDot("text with html:\r\n<p>another paragraph</p>"))
                .isEqualTo("text with html:\r\n<p>another paragraph</p>");
    }

    @Test
    public void join() {
        assertThat(FormatUtil.join("")).isEqualTo("");
        assertThat(FormatUtil.join(", ")).isEqualTo("");
        assertThat(FormatUtil.join(", ", null, null)).isEqualTo("");
        assertThat(FormatUtil.join(", ", "1")).isEqualTo("1");
        assertThat(FormatUtil.join(", ", "1", null, "2", "  ", "3")).isEqualTo("1, 2, 3");
    }
}
