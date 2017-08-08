package capital.scalable.restdocs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FieldUtilTest {
    @Test
    public void fromGetter() throws Exception {
        assertThat(FieldUtil.fromGetter("GetField"), is("GetField"));
        assertThat(FieldUtil.fromGetter("getField"), is("field"));
        assertThat(FieldUtil.fromGetter("IsField"), is("IsField"));
        assertThat(FieldUtil.fromGetter("isField"), is("field"));
    }

    @Test
    public void isGetter() throws Exception {
        assertThat(FieldUtil.isGetter("GetField"), is(false));
        assertThat(FieldUtil.isGetter("getField"), is(true));
        assertThat(FieldUtil.isGetter("IsField"), is(false));
        assertThat(FieldUtil.isGetter("isField"), is(true));
    }
}