package pl.cleankod.util.domain;

import pl.cleankod.util.Either;

import java.util.Currency;

public class AppErrors {

    public static class Base extends RuntimeException {

        private String message;

        private Base() {
            //
        }

        private Base(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class IllegalArgument extends Base {
        public IllegalArgument(String message) {
            super(message);
        }
    }

    public static class NotFound extends Base {
        public NotFound(String message) {
            super(message);
        }
    }

    public static class Provider extends Base {
        public Provider(String message) {
            super(message);
        }
    }

    public static class Conversion extends Base {

        public Conversion(Currency sourceCurrency, Currency targetCurrency) {
            super(String.format("Cannot convert currency from %s to %s.", sourceCurrency, targetCurrency));
        }
    }

    //

    public static NotFound notFound(String message) {
        return new NotFound(message);
    }

    public static <T> Either<NotFound, T> eitherNotFound(String message) {
        return Either.left(notFound(message));
    }

    private static Conversion conversion(Currency sourceCurrency, Currency targetCurrency) {
        return new Conversion(sourceCurrency, targetCurrency);
    }

    public static <T> Either<Conversion, T> eitherConversion(Currency sourceCurrency, Currency targetCurrency) {
        return Either.left(conversion(sourceCurrency, targetCurrency));
    }

    public static Provider provider(String message) {
        return new Provider(message);
    }

    public static IllegalArgument illegalArgument(String message) {
        return new IllegalArgument(message);
    }

    public static <T> Either<IllegalArgument, T> eitherIllegalArgument(String message) {
        return Either.left(illegalArgument(message));
    }

}

