package pl.cleankod.exchange.entrypoint.model;

import pl.cleankod.exchange.exceptions.BaseException;

public record ApiError(String message, BaseException.ErrorCode code) {
}
