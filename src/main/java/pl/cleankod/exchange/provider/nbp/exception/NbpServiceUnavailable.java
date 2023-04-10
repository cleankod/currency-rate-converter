package pl.cleankod.exchange.provider.nbp.exception;

public class NbpServiceUnavailable extends NbpApiRuntimeError {
    public NbpServiceUnavailable(String message) {
        super(message);
    }
}
