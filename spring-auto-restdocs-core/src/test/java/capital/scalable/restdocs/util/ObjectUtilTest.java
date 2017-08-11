package capital.scalable.restdocs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ObjectUtilTest {
    @Test
    public void arrayToString() throws Exception {
        assertThat(ObjectUtil.arrayToString(new Object[]{"1", 2, "3"}), is("[1, 2, 3]"));
    }
}