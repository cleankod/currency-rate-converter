package pl.cleankod.exchange.core.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.repository.AccountRepository;
import pl.cleankod.exchange.exception.ResourceNotFoundException;
import pl.cleankod.exchange.exception.error_type.AccountErrorType;


public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LogManager.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;

    public AccountServiceImpl(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account execute(final Account.Id id) {
        return accountRepository.find(id)
                .orElseThrow(() -> {
                    LOGGER.error("Account(id={}) not found", id.value());
                    return new ResourceNotFoundException(AccountErrorType.NOT_FOUND,
                            String.format("Account(id=%s) not found", id));
                });
    }

    @Override
    public Account execute(final Account.Number number) {
        LOGGER.info("execute(number): Trying to retrieve account with number={}", number.value());
        return accountRepository.find(number)
                .orElseThrow(() -> {
                    LOGGER.error("Account(number={}) not found", number.value());
                    return new ResourceNotFoundException(AccountErrorType.NOT_FOUND,
                            String.format("Account(number=%s) not found", number));
                });
    }

}
