package testdata

import java.math.BigDecimal

/**
 * Class documentation
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