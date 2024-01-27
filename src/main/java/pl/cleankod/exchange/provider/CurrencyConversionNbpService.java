package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.model.Result;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.logging.Logger;

public class CurrencyConversionNbpService implements CurrencyConversionService {
		
		private final ExchangeRatesNbpClient exchangeRatesNbpClient;
		
		private final Logger logger = Logger.getLogger(CurrencyConversionNbpService.class.getName());
		
		private static final String MID_RATE_ZERO_MSG = "Mid rate cannot be zero for conversion";
		
		private static final String INVALID_RATE_OR_EMPTY_MSG = "Invalid or empty rate data received";
		
		public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
				this.exchangeRatesNbpClient = exchangeRatesNbpClient;
		}
		
		@Override
		public Result<Money, String> convert(Money money, Currency targetCurrency) {
				try {
						RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
						if (rateWrapper == null || rateWrapper.rates() == null || rateWrapper.rates().isEmpty()) {
								logger.warning(INVALID_RATE_OR_EMPTY_MSG);
								return Result.error(INVALID_RATE_OR_EMPTY_MSG);
						} else {
								BigDecimal midRate = rateWrapper.rates().get(0).mid();
								if (midRate.compareTo(BigDecimal.ZERO) == 0) {
										logger.warning(MID_RATE_ZERO_MSG);
										return Result.error(MID_RATE_ZERO_MSG);
								}
								BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP);
								return Result.success(new Money(calculatedRate, targetCurrency));
						}
				} catch (Exception e) {
						return Result.error("Exception occurred during currency conversion: " + e.getMessage());
				}
		}
}