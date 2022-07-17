package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.model.AccountNotFoundException;
import pl.cleankod.exchange.entrypoint.model.AccountResponse;

import java.util.Currency;

public interface AccountService {

    AccountResponse findAccountBy(Account.Id id, String targetCurrency, String correlationId) throws AccountNotFoundException;

    AccountResponse findAccountBy(Account.Number id, String targetCurrency, String correlationId) throws AccountNotFoundException;
}
