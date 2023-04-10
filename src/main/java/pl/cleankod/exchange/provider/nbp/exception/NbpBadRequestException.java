package pl.cleankod.exchange.provider.nbp.exception;

public class NbpBadRequestException extends NbpApiRuntimeError {
    public NbpBadRequestException(String message) {
        super(message);
    }
}
