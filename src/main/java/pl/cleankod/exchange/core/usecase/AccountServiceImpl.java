package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

	private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
	private final FindAccountUseCase findAccountUseCase;

	public AccountServiceImpl(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
		this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
		this.findAccountUseCase = findAccountUseCase;
	}

	@Override
	public Optional<Account> findAccount(String accountId, Optional<String> currencyOpt) {
		Account.Id id = Account.Id.of(accountId);
		return currencyOpt
				.map(currency -> findAccountAndConvertCurrencyUseCase.execute(id, Currency.getInstance(currency)))
				.orElseGet(() -> findAccountUseCase.execute(id));
	}

	@Override
	public Optional<Account> findAccountByNumber(String accountNumber, Optional<String> currencyOpt) {
		Account.Number number = Account.Number.of(URLDecoder.decode(accountNumber, StandardCharsets.UTF_8));
		return currencyOpt
				.map(currency -> findAccountAndConvertCurrencyUseCase.execute(number, Currency.getInstance(currency)))
				.orElseGet(() -> findAccountUseCase.execute(number));
	}
}
