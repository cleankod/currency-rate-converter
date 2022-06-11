package pl.cleankod.exchange.core.aggregator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.dto.AccountDto;
import pl.cleankod.exchange.core.mapper.AccountMapper;
import pl.cleankod.exchange.core.service.AccountService;
import pl.cleankod.exchange.core.service.CurrencyConversionService;
import pl.cleankod.exchange.exception.InvalidDataException;
import pl.cleankod.exchange.exception.error_type.AccountErrorType;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.UUID;

public class AccountAggregatorImpl implements AccountAggregator {

    private static final Logger LOGGER = LogManager.getLogger(AccountAggregatorImpl.class);

    private final AccountService accountService;
    private final CurrencyConversionService currencyConverterService;

    private final AccountMapper accountMapper;

    public AccountAggregatorImpl(final AccountService accountService,
                                 final CurrencyConversionService currencyConverterService,
                                 final AccountMapper accountMapper) {
        this.accountService = accountService;
        this.currencyConverterService = currencyConverterService;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountDto findAccountById(final UUID id, final String currency) {
        LOGGER.info("findAccountById(id, currency): Trying to retrieve account with id={}", id);
        final Account account = accountService.execute(Account.Id.of(id));
        LOGGER.info("findAccountById(id, currency): Account with id={} retrieved successfully", id);
        return getAccountDto(currency, account);
    }

    @Override
    public AccountDto findAccountByNumber(final String number, final String currency) {
        LOGGER.info("findAccountByNumber(number, currency): Trying to retrieve account with number={}", number);
        final Account account = accountService.execute(getAccountNumber(number));
        LOGGER.info("findAccountByNumber(number, currency): Account with number={} retrieved successfully", number);
        return getAccountDto(currency, account);
    }

    private Account.Number getAccountNumber(final String number) {
        try {
            return Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        } catch (final IllegalArgumentException ex) {
            LOGGER.error("Failed to create account number from value={}. Reason: {}", number, ex.getMessage(), ex);
            throw new InvalidDataException(AccountErrorType.FAILED_TO_DECODE_ACC_NUMBER,
                    String.format("Failed to create account number from value=%s.", number));
        }
    }

    private AccountDto getAccountDto(final String currency, final Account account) {
        if (StringUtils.hasLength(currency)) {
            return accountMapper.toDto(
                    account, currencyConverterService.convert(account.balance(), Currency.getInstance(currency)));
        } else {
            return accountMapper.toDto(account, account.balance());
        }
    }

}
