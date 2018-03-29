package testdata

import java.math.BigDecimal

/**
 * Class documentation
 *
 * Next paragraph
 *
 * * Item 1
 * * Item 2
 */
data class KotlinDataClass(
        /**
         * Documentation for text property
         *
         * @see KotlinDataClass
         * @deprecated Use something else
         * @title Custom tag
         */
        val text: String,
        /**
         * Documentation for number property
         */
        val number: BigDecimal) {

    /**
     * Function add
     *
     * [some link](http://some-url.com)
     *
     * @param a First param a
     * @param b Second param b
     * @see KotlinDataClass
     * @deprecated Use something else
     * @title Custom tag
     */
    fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a + b

    /**
     * A nested class
     */
    data class NestedClass(
            /**
             * Field on a nested class
             */
            val someField: String)
}