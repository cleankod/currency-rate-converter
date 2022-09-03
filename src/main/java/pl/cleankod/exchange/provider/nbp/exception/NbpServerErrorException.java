package pl.cleankod.exchange.provider.nbp.exception;

public class NbpServerErrorException extends RuntimeException {
    public NbpServerErrorException(String message) {
        super(message);
    }
}
