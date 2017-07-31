package capital.scalable.restdocs.javadoc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JavadocUtilTest {

    private static final String LINE_BREAK_ASCIIDOC = " +\n";

    @Test
    public void convertLineBreaks() {
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
        assertThat(JavadocUtil.convertFromJavadoc(actual, LINE_BREAK_ASCIIDOC), is(expected));
    }

    @Test
    public void convertBulletedList() {
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
        assertThat(JavadocUtil.convertFromJavadoc(actual, LINE_BREAK_ASCIIDOC), is(expected));
    }
}