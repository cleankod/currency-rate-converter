package pl.cleankod.util;

public class UnknownCurrencyException extends IllegalArgumentException {
    public UnknownCurrencyException(String s) {
        super(String.format("Unknown currency: %s.", s));
    }
}
