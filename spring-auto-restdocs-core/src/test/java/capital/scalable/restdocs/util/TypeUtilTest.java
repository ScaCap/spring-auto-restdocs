package capital.scalable.restdocs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class TypeUtilTest {
    @Test
    public void determineTypeName() throws Exception {
        assertThat(TypeUtil.determineTypeName(byte.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Byte.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(short.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Short.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(long.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Long.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(int.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Integer.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(BigInteger.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(float.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(Float.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(double.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(Double.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(BigDecimal.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(char.class), is("String"));
        assertThat(TypeUtil.determineTypeName(Character.class), is("String"));
        assertThat(TypeUtil.determineTypeName(boolean.class), is("Boolean"));
        assertThat(TypeUtil.determineTypeName(Boolean.class), is("Boolean"));
    }

    @Test
    public void isPrimitive() throws Exception {
        assertThat(TypeUtil.isPrimitive(TestClass.class, "primitive"), is(true));
        assertThat(TypeUtil.isPrimitive(TestClass.class, "wrapper"), is(false));
    }

    static class TestClass {
        private boolean primitive;
        private Boolean wrapper;
    }
}