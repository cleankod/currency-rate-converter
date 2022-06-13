package pl.cleankod.exchange.core.domain.rate;

public class MidRateCanNotBeLowerThenZeroException extends IllegalArgumentException {

    public MidRateCanNotBeLowerThenZeroException(String cause) {
        super(cause);
    }
}
