package pl.cleankod.exchange.entrypoint.model;

public class NbpApiException extends Exception {
    NbpExceptionMessage nbpExceptionMessage;

    public NbpApiException(NbpExceptionMessage nbpExceptionMessage) {
        this.nbpExceptionMessage = nbpExceptionMessage;
    }

    public NbpExceptionMessage getNbpExceptionMessage() {
        return nbpExceptionMessage;
    }
}
