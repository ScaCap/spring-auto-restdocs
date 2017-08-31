package capital.scalable.restdocs.example.items;

import java.math.BigDecimal;

/**
 * Simplified version of Java Money (JDK8 only) to demonstrate custom serializers.
 */
public class Money {

    private final String currencyCode;

    private final BigDecimal number;

    private Money(BigDecimal number, String currencyCode) {
        this.currencyCode = currencyCode;
        this.number = number;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getNumberStripped() {
        if (this.number.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return this.number.stripTrailingZeros();
    }

    public static Money of(BigDecimal number, String currencyCode) {
        return new Money(number, currencyCode);
    }
}
