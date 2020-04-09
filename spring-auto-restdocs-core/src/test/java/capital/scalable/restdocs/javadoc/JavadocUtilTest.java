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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import capital.scalable.restdocs.util.TemplateFormatting;
import org.junit.Test;

public class JavadocUtilTest {

    @Test
    public void convertLineBreaksAsciidoc() {
        String actual = "" +
                "First line is ok" +
                "<br>  second line should be trimmed  " +
                "<br/>\n\nthis is just \none\n\n line " +
                "<p> first paragraph</p>" +
                "<p> second paragraph \n \n  ";
        String expected = "" +
                "First line is ok" +
                " +\nsecond line should be trimmed" +
                " +\nthis is just one line" +
                "\n\nfirst paragraph\n\n" +
                "\n\nsecond paragraph";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.ASCIIDOC),
                is(expected));
    }

    @Test
    public void convertLineBreaksMarkdown() {
        String actual = "" +
                "First line is ok" +
                "<br>  second line should be trimmed  " +
                "<br/>\n\nthis is just \none\n\n line " +
                "<p> first paragraph</p>" +
                "<p> second paragraph \n \n  ";
        String expected = "" +
                "First line is ok" +
                "<br>second line should be trimmed" +
                "<br>this is just one line" +
                "\n\nfirst paragraph\n\n" +
                "\n\nsecond paragraph";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.MARKDOWN),
                is(expected));
    }

    @Test
    public void convertBulletedListAsciidoc() {
        String actual = "" +
                "<ul>" +
                "<li>first bullet</li>" +
                "<li>second bullet</li>" +
                "</ul>some text" +
                "<li>standalone li" +
                "<ul>" +
                "<li>without end works as well";
        String expected = "" +
                "\n" +
                "\n- first bullet" +
                "\n- second bullet" +
                "\n\nsome text" +
                "\n- standalone li" +
                "\n" +
                "\n- without end works as well";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.ASCIIDOC),
                is(expected));
    }

    @Test
    public void convertBulletedListMarkdown() {
        String actual = "" +
                "<ul>" +
                "<li>first bullet</li>" +
                "<li>second bullet</li>" +
                "</ul>some text" +
                "<li>standalone li" +
                "<ul>" +
                "<li>without end works as well";
        String expected = "" +
                "\n" +
                "\n- first bullet" +
                "\n- second bullet" +
                "\n\nsome text" +
                "\n- standalone li" +
                "\n" +
                "\n- without end works as well";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.MARKDOWN),
                is(expected));
    }

    @Test
    public void convertStylingAsciidoc() {
        String actual = "<b>bold</b>normal<i>italics</i>";
        String expected = "**bold**normal__italics__";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.ASCIIDOC),
                is(expected));
    }

    @Test
    public void convertStylingMarkdown() {
        String actual = "<b>bold</b>normal<i>italics</i>";
        String expected = "**bold**normal*italics*";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.MARKDOWN),
                is(expected));
    }

    @Test
    public void convertLinkAsciidoc() {
        String actual = "<a href=\"https://github.com\">GitHub</a>";
        String expected = "link:https://github.com[GitHub]";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.ASCIIDOC),
                is(expected));
    }

    @Test
    public void convertLinkMarkdown() {
        String actual = "<a href=\"https://github.com\">GitHub</a>";
        String expected = "[GitHub](https://github.com)";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.MARKDOWN),
                is(expected));
    }
}
