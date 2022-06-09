package pl.cleankod.exchange.core.domain;

public class MoneyCanNotBeLowerThenZeroException extends IllegalArgumentException {

    public MoneyCanNotBeLowerThenZeroException(String error) {
        super(error);
    }
}
