package pl.cleankod.exchange.provider.nbp;

import feign.Response;
import feign.codec.ErrorDecoder;
import pl.cleankod.exchange.provider.nbp.exceptions.ClientAuthorizationException;
import pl.cleankod.exchange.provider.nbp.exceptions.CurrencyNotFoundException;
import pl.cleankod.exchange.provider.nbp.exceptions.InvalidCurrencyRequestException;
import pl.cleankod.exchange.provider.nbp.exceptions.NbpServerException;

public class CustomNbpErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder defaultErrorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		return switch (response.status()) {
			case 404 -> new CurrencyNotFoundException("Currency resource not found");
			case 400 -> new InvalidCurrencyRequestException("Invalid request to external API");
			case 401, 403 -> new ClientAuthorizationException("Authorization issue with external API");
			case 500, 502, 503, 504 -> new NbpServerException("External API error");
			default -> defaultErrorDecoder.decode(methodKey, response);
		};
	}
}


