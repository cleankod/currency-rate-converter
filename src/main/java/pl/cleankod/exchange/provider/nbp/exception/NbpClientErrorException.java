package pl.cleankod.exchange.provider.nbp.exception;

public class NbpClientErrorException extends IllegalStateException {

    public NbpClientErrorException(String statusCode, String additionalInfo) {
        super(String.format("Error on NBP request." +
                " Status code = [" + statusCode + "]," +
                " additional information = ["+ additionalInfo +"].")
        );
    }

}
