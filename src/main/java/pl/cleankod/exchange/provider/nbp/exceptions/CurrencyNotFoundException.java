package pl.cleankod.exchange.provider.nbp.exceptions;

public class CurrencyNotFoundException extends RuntimeException {
	public CurrencyNotFoundException(String message) {
		super(message);
	}
}
