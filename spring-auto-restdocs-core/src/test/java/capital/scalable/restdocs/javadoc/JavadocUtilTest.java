package capital.scalable.restdocs.javadoc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JavadocUtilTest {

    @Test
    public void convertFromJavadoc() {
        String actual = "First line is ok" +
                "<br>  second line should be trimmed  " +
                "<br/>\n\nthis is just \none\n\n line " +
                "<p> first paragraph</p>" +
                "<p> second paragraph \n \n  ";
        String expected = "First line is ok" +
                "|LB|second line should be trimmed" +
                "|LB|this is just one line" +
                "|LB||LB|first paragraph" +
                "|LB||LB|second paragraph";
        assertThat(JavadocUtil.convertFromJavadoc(actual, "|LB|"), is(expected));
    }
}