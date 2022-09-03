package pl.cleankod.exchange.provider.nbp.exception;

public class NbpUnauthorizedException extends RuntimeException {
    public NbpUnauthorizedException(String message) {
        super(message);
    }
}
