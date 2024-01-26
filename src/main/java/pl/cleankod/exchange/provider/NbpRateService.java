package pl.cleankod.exchange.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public class NbpRateService {

	private static final Logger log = LoggerFactory.getLogger(NbpRateService.class);

	private final ExchangeRatesNbpClient exchangeRatesNbpClient;

	public NbpRateService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
		this.exchangeRatesNbpClient = exchangeRatesNbpClient;
	}

	@Cacheable(value = "nbpRates", key = "#currencyCode")
	public RateWrapper getRateWrapper(String table, String currencyCode) {
		log.info("Requesting rates for table {} & currency {}", table, currencyCode);
		return exchangeRatesNbpClient.fetch(table, currencyCode);
	}

}
