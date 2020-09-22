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
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void convertBoldStylingAsciidoc() {
        String actual = "before<b>bold</b>normal<strong>bold2</strong>after";
        String expected = "before**bold**normal**bold2**after";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.ASCIIDOC),
                is(expected));
    }

    @Test
    public void convertItalicsStylingAsciidoc() {
        String actual = "before<i>italics</i>normal<em>italics2</em>after";
        String expected = "before__italics__normal__italics2__after";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.ASCIIDOC),
                is(expected));
    }

    @Test
    public void convertCodeStylingAsciidoc() {
        String actual = "before <code>code()</code> after";
        String expected = "before `code()` after";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.ASCIIDOC),
                is(expected));
    }

    @Test
    public void convertBoldStylingMarkdown() {
        String actual = "before<b>bold</b>normal<strong>bold2</strong>after";
        String expected = "before**bold**normal**bold2**after";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.MARKDOWN),
                is(expected));
    }

    @Test
    public void convertItalicsStylingMarkdown() {
        String actual = "before<i>italics</i>normal<em>italics2</em>after";
        String expected = "before*italics*normal*italics2*after";
        assertThat(JavadocUtil.convertFromJavadoc(actual, TemplateFormatting.MARKDOWN),
                is(expected));
    }

    @Test
    public void convertCodeStylingMarkdown() {
        String actual = "before <code>code()</code> after";
        String expected = "before `code()` after";
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
