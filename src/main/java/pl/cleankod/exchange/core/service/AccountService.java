package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;

public interface AccountService {

    Account execute(final Account.Id id);

    Account execute(final Account.Number number);

}
