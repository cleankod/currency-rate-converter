package pl.cleankod.exchange.exception.error_type;

public enum AccountErrorType implements ErrorType {

    NOT_FOUND(Value.NOT_FOUND, "User not found"),
    FAILED_TO_DECODE_ACC_NUMBER(Value.FAILED_TO_DECODE_ACC_NUMBER, "Failed to decode account number"),
    INVALID_ACCOUNT_NUMBER(Value.INVALID_ACCOUNT_NUMBER, "Invalid account number");
    private final String code;
    private final String description;

    AccountErrorType(final String code, final String description) {
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

        private static final String PREFIX = "account_";
        public static final String NOT_FOUND = PREFIX + "not_found";
        public static final String FAILED_TO_DECODE_ACC_NUMBER = PREFIX + "failed_to_decode_number";
        public static final String INVALID_ACCOUNT_NUMBER = PREFIX + "invalid_number";

    }

}
