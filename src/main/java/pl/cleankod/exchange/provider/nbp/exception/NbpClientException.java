package pl.cleankod.exchange.provider.nbp.exception;

public class NbpClientException extends RuntimeException {

    public NbpClientException(String currency, String table) {
        super(String.format("Nbp service error. Cannot get currency %s from table %s.", currency, table));
    }

}