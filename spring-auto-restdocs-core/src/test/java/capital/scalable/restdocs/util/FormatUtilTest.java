package capital.scalable.restdocs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FormatUtilTest {
    @Test
    public void arrayToString() throws Exception {
        assertThat(FormatUtil.arrayToString(new Object[]{"1", 2, "3"}), is("[1, 2, 3]"));
    }

    @Test
    public void addDot() throws Exception {
        assertThat(FormatUtil.addDot(null), is(nullValue()));
        assertThat(FormatUtil.addDot(""), is(""));
        assertThat(FormatUtil.addDot("."), is("."));
        assertThat(FormatUtil.addDot("text."), is("text."));
        assertThat(FormatUtil.addDot("text"), is("text."));
    }
}