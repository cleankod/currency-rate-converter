package pl.cleankod.exchange.core.gateway;

import org.springframework.stereotype.Service;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.model.AccountNotFoundException;
import pl.cleankod.exchange.entrypoint.model.AccountResponse;

public interface AccountService {

    AccountResponse findAccountBy(Account.Id id, String targetCurrency) throws AccountNotFoundException;

    AccountResponse findAccountBy(Account.Number id, String targetCurrency) throws AccountNotFoundException;
}
