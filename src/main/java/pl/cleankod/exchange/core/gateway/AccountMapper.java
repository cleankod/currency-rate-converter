package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.dto.AccountDTO;

import java.util.Currency;

public interface AccountMapper {

    AccountDTO mapAccountToDTO(Account account);

    AccountDTO mapAccountToDTO(Account account, Currency targetCurrency);
}
