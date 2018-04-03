import java.math.BigDecimal;

/**
 * Javadoc on a Java class
 * <p>
 * Next paragraph
 *
 * <ul>
 *     <li>Item 1</li>
 *     <li>Item 2</li>
 * </ul>
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
     * @see <a href="http://some-url.com">some link</a>
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