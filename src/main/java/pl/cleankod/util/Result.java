package pl.cleankod.util;

import java.util.Objects;
import java.util.function.Function;

public record Result<S, F>(S successfulValue, F failValue, boolean isSuccessful) {
    private static final String FAIL_VALUE_CANNOT_BE_NULL = "The fail value cannot be null.";
    private static final String SUCCESS_VALUE_CANNOT_BE_NULL = "The success value cannot be null.";

    public static <S, F> Result<S, F> successful(S value) {
        return new Result<>(Objects.requireNonNull(value, SUCCESS_VALUE_CANNOT_BE_NULL), null, true);
    }

    public static <S, F> Result<S, F> fail(F value) {
        return new Result<>(null, Objects.requireNonNull(value, FAIL_VALUE_CANNOT_BE_NULL), false);
    }

    public boolean isFail() {
        return !isSuccessful;
    }

    public <U> U fold(Function<S, U> successMapper, Function<F, U> failureMapper) {
        if (isFail()) {
            return failureMapper.apply(this.failValue);
        }
        return successMapper.apply(this.successfulValue);
    }

}
