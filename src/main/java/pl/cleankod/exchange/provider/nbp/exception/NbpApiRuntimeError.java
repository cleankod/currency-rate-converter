package pl.cleankod.exchange.provider.nbp.exception;

public class NbpApiRuntimeError extends RuntimeException {
    public NbpApiRuntimeError(String message) {
        super(message);
    }
}
