package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.model.Result;

import java.util.Currency;
import java.util.Optional;
import java.util.logging.Logger;

public class ConvertCurrencyUseCase {
		
		private final AccountRepository accountRepository;
		private final CurrencyConversionService currencyConversionService;
		private final Currency baseCurrency;
		
		private final Logger logger = Logger.getLogger(ConvertCurrencyUseCase.class.getName());
		
		
		public ConvertCurrencyUseCase(AccountRepository accountRepository,
																	CurrencyConversionService currencyConversionService,
																	Currency baseCurrency) {
				this.accountRepository = accountRepository;
				this.currencyConversionService = currencyConversionService;
				this.baseCurrency = baseCurrency;
		}
		
		public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
				return processAccountConversion(accountRepository.find(id), targetCurrency);
		}
		
		public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
				return processAccountConversion(accountRepository.find(number), targetCurrency);
		}
		
		private Optional<Account> processAccountConversion(Optional<Account> accountOpt, Currency targetCurrency) {
				return accountOpt.flatMap(account -> {
						Result<Money, String> conversionResult = convert(account.balance(), targetCurrency);
						if (conversionResult.isSuccess()) {
								logger.info("Conversion successful for account: " + account.id());
								return Optional.of(new Account(account.id(), account.number(), conversionResult.value()));
						} else {
								logger.warning("Conversion error for account " + account.id() + ": " + conversionResult.error());
								return Optional.empty();
						}
				});
		}
		
		private Result<Money, String> convert(Money money, Currency targetCurrency) {
				if (!baseCurrency.equals(targetCurrency)) {
						return currencyConversionService.convert(money, targetCurrency);
				}
				
				if (!money.currency().equals(targetCurrency)) {
						String errorMessage = "Cannot convert currency from " + money.currency() + " to " + targetCurrency;
						logger.warning(errorMessage);
						return Result.error(errorMessage);
				}
				
				return Result.success(money);
		}
}