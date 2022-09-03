package pl.cleankod.exchange.provider.nbp.exception;

import pl.cleankod.exchange.core.exception.NotFoundException;

public class NbpNotFountException extends NotFoundException {
    public NbpNotFountException(String message) {
        super(message);
    }
}
