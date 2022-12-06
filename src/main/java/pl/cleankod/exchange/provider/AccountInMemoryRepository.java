package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.util.Optional;
import java.util.Set;

public class AccountInMemoryRepository implements AccountRepository {

    private final Set<Account> accounts = Set.of(
            new Account(
                    Account.Id.of("fa07c538-8ce4-11ec-9ad5-4f5a625cd744"),
                    Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                    Money.of("123.45", "PLN")
            ),
            new Account(
                    Account.Id.of("78743420-8ce9-11ec-b0d0-57b77255c208"),
                    Account.Number.of("75 1240 2034 1111 0000 0306 8582"),
                    Money.of("456.78", "EUR")
            ),
            new Account(
                    Account.Id.of("0bb1378a-8d43-4071-b44c-67a0f356d9bb"),
                    Account.Number.of("11 1111 1111 1111 1111 1111 1111"),
                    Money.of("2.01", "PLN")
            ),
            new Account(
                    Account.Id.of("bce2bb32-4582-4734-9273-613804acf36d"),
                    Account.Number.of("21 1111 1111 1111 1111 1111 1111"),
                    Money.of("2.03", "PLN")
            )
    );

    @Override
    public Optional<Account> find(Account.Id id) {
        return accounts.stream()
                .filter(account -> account.id().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Account> find(Account.Number number) {
        return accounts.stream()
                .filter(account -> account.number().equals(number))
                .findFirst();
    }
}
