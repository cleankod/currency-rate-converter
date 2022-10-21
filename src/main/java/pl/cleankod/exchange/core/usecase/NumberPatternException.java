package pl.cleankod.exchange.core.usecase;

public class NumberPatternException extends RuntimeException {

    public NumberPatternException(String value, String pattern) {
        super(String.format("The account number: %s does not match the pattern %s.", value, pattern));
    }
}
