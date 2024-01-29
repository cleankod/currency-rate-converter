package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {
		
		private final ConvertCurrencyUseCase convertCurrencyUseCase;
		
		private final FindAccountUseCase findAccountUseCase;
		
		public AccountServiceImpl(ConvertCurrencyUseCase convertCurrencyUseCase,
															FindAccountUseCase findAccountUseCase) {
				this.convertCurrencyUseCase = convertCurrencyUseCase;
				this.findAccountUseCase = findAccountUseCase;
		}
		
		@Override
		public Optional<Account> findAccountById(String accountId, Optional<String> currencyOptional) {
				Account.Id id = Account.Id.of(accountId);
				return currencyOptional
                .map(currency ->
                        convertCurrencyUseCase.execute(id, Currency.getInstance(currency)))
                .orElseGet(() ->
                        findAccountUseCase.execute(id));
		}
		
		@Override
		public Optional<Account> findAccountByNumber(String number, Optional<String> currencyOptional) {
				Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return currencyOptional
                .map(currency ->
                        convertCurrencyUseCase.execute(accountNumber, Currency.getInstance(currency)))
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber));
		}
}