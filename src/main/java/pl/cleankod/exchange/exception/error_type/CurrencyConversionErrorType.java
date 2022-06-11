package pl.cleankod.exchange.exception.error_type;

public enum CurrencyConversionErrorType implements ErrorType {

    CURRENCY_CONVERSION_FAIL(Value.CURRENCY_CONVERSION_FAIL, "Can't convert currencies"),
    FAILED_TO_DESERIALIZE(Value.FAILED_TO_DESERIALIZE, "Failed to deserialize"),
    FAILED_TO_RETRIEVE_EXCHANGE_RATE(Value.FAILED_TO_RETRIEVE_EXCHANGE_RATE, "Failed to retrieve exchange rate"),
    NO_RATES_FOUND(Value.NO_RATES_FOUND, "No rates found");

    private final String code;
    private final String description;

    CurrencyConversionErrorType(final String code, final String description) {
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

        private static final String PREFIX = "currency_conversion_";

        public static final String CURRENCY_CONVERSION_FAIL = PREFIX + "fail";
        public static final String FAILED_TO_DESERIALIZE = PREFIX + "failed_to_deserialize";
        public static final String FAILED_TO_RETRIEVE_EXCHANGE_RATE = PREFIX + "failed_to_retrieve_exchange";
        public static final String NO_RATES_FOUND = PREFIX + "no_rates_found";
    }

}
