import java.math.BigDecimal;

/**
 * Javadoc on a Java class
 */
public class JavaClass {

    /**
     * A Java field
     *
     * @see JavaClass
     * @deprecated Use something else
     * @title Custom tag
     */
    public String someField;

    /**
     * Method add
     *
     * @param a First param a
     * @param b Second param b
     * @see JavaClass
     * @deprecated Use something else
     * @title Custom tag
     */
    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    /**
     * A nested Java class
     */
    public static class NestedJavaClass {

    }
}