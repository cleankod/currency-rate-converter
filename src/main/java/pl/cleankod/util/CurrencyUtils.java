package pl.cleankod.util;

import java.util.Currency;


public class CurrencyUtils {

    /**
     * Returns the {@code Currency} instance for the given currency code.
     * Wraps Currency.getInstance(currencyCode) and replaces IllegalArgumentException with
     * UnknownCurrencyException to ease error handling and allows null argument.
     * @param currencyCode the ISO 4217 code of the currency
     * @return the {@code Currency} instance for the given currency code or null if given currencyCode is null
     * @throws    UnknownCurrencyException if {@code currencyCode} is not
     * a supported ISO 4217 code.
     */
    public static Currency stringToCurrencyOrNull(String currencyCode) {
        if (currencyCode == null) {
            return null;
        }
        try {
            return Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException exception) {
            throw new UnknownCurrencyException(currencyCode);
        }
    }
}
