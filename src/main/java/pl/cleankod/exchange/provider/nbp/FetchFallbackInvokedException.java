package pl.cleankod.exchange.provider.nbp;

public class FetchFallbackInvokedException extends RuntimeException {
    public FetchFallbackInvokedException (String message, Throwable cause) {
        super(message, cause);
    }
}