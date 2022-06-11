package pl.cleankod.exchange.exception.error_type;

public enum ExchangeRateProviderErrorType implements ErrorType {

    NOT_FOUND(ExchangeRateProviderErrorType.Value.NOT_FOUND, "Exchange rate provider resource not found"),
    INVALID_REQUEST(ExchangeRateProviderErrorType.Value.INVALID_REQUEST, "Exchange rate provider request invalid");

    private final String code;
    private final String description;

    ExchangeRateProviderErrorType(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public final class Value {

        private static final String PREFIX = "exchange_rate_provider_";
        public static final String NOT_FOUND = PREFIX + "resource_not_found";
        public static final String INVALID_REQUEST = PREFIX + "invalid_request";

    }

}

