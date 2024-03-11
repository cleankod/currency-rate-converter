package pl.cleankod.exchange.core.usecase;

import static pl.cleankod.util.domain.Failures.ACCOUNT_NOT_FOUND;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.util.Optional;
import pl.cleankod.util.domain.Failure;
import pl.cleankod.util.domain.Result;

public class FindAccountUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindAccountUseCase.class);

    private final AccountRepository accountRepository;

    public FindAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Result<Account> execute(Account.Id id) {
        LOGGER.info("Processing Account found for the given id: {}", id);
        return getAccountResult(accountRepository.find(id));
    }


    public Result<Account> execute(Account.Number number) {
        LOGGER.info("Processing Account found for the given number: {}", number);
        return getAccountResult(accountRepository.find(number));
    }

    private static Result<Account> getAccountResult(Optional<Account> accountOptional) {
        return accountOptional
                .map(Result::ok)
                .orElseGet(() -> {
                    LOGGER.error("Account not found for the given id or number");
                    return Result.failure(new Failure(ACCOUNT_NOT_FOUND, "Account not found"));});
    }
}
