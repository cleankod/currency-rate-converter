package pl.cleankod.exchange.provider.nbp.model;

public class NbpOperationFailedReason {
    private final String message;

    private NbpOperationFailedReason(String message) {
        this.message = message;
    }

    public static NbpOperationFailedReason getRateWrapperFailed(String currencyCode) {
        var message = String.format("Cannot get rate wrapper for currency code %s", currencyCode);
        return new NbpOperationFailedReason(message);
    }

    public static NbpOperationFailedReason decodeNbpServiceResponseFailed(String currencyCode) {
        var message = String.format("Rate wrapper for currency code %s not found in exchange rates NBP client", currencyCode);
        return new NbpOperationFailedReason(message);
    }

    public String getMessage() {
        return message;
    }
}
