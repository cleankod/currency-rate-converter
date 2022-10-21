package pl.cleankod.exchange.core.usecase;

import org.springframework.stereotype.Service;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.dto.AccountDTO;
import pl.cleankod.exchange.core.gateway.AccountMapper;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.util.Currency;
import java.util.Optional;

@Service
public class FindAccountAndConvertCurrencyUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public FindAccountAndConvertCurrencyUseCase(AccountRepository accountRepository,
                                                AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public Optional<AccountDTO> execute(Account.Id id, Currency targetCurrency) {
        return Optional.ofNullable(accountRepository.find(id)
                .map(account -> accountMapper.mapAccountToDTO(account, targetCurrency))
                .orElseThrow(() -> new UserNotFoundException(id.value())));
    }

    public Optional<AccountDTO> execute(Account.Number number, Currency targetCurrency) {
        return Optional.ofNullable(accountRepository.find(number)
                .map(account -> accountMapper.mapAccountToDTO(account, targetCurrency))
                .orElseThrow(() -> new UserNotFoundException(number.value())));
    }

}
