package testdata

import java.math.BigDecimal

/**
 * Class documentation
 *
 * Next paragraph
 * UTF 8 test: 我能吞下玻璃而不伤身体。Árvíztűrő tükörfúrógép
 *
 * * Item 1
 * * Item 2
 */
data class KotlinDataClass(
        /**
         * Documentation for _text_ property (テスト)
         *
         * @see KotlinDataClass
         * @deprecated Use something else (テスト)
         * @title Custom tag
         */
        val text: String,
        /**
         * Documentation for *number* property
         */
        val number: BigDecimal) {

    /**
     * Function add (テスト)
     *
     * [some link](http://some-url.com)
     *
     * @param a First param **a**
     * @param b Second param __b__
     * @see KotlinDataClass
     * @deprecated Use something else
     * @title Custom tag
     */
    fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a + b

    /**
     * A nested class
     *
     * ``There is a literal backtick (`) here.``
     */
    data class NestedClass(
            /**
             * Field on a nested class with some code: `println("foo")`
             */
            val someField: String)
}