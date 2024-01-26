package pl.cleankod.exchange.provider.nbp.exceptions;

public class InvalidCurrencyRequestException extends RuntimeException {
	public InvalidCurrencyRequestException(String message) {
		super(message);
	}
}
