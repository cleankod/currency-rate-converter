package pl.cleankod.exchange.provider.nbp.model;

public class ExternalClientParameterException extends IllegalStateException {

    public ExternalClientParameterException(String reason) {
        super(reason);
    }
}
