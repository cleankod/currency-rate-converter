package pl.cleankod.exchange.core.mapper

import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.dto.AccountDTO
import pl.cleankod.exchange.core.gateway.AccountMapper
import pl.cleankod.exchange.core.service.CircuitBreakerImpl
import pl.cleankod.exchange.core.service.ServiceConvertorImpl
import pl.cleankod.exchange.provider.CurrencyConversionStubService
import spock.lang.Specification

class AccountMapperImplTest extends Specification {

    def "should create DTO from entity"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744";
        AccountMapper accountMapper = new AccountMapperImpl(
                new ServiceConvertorImpl(
                        new CircuitBreakerImpl(new CurrencyConversionStubService()),
                        Currency.getInstance("PLN")
                )
        );
        Account account = new Account(
                Account.Id.of(accountId),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of("123.45", "PLN")
        )

        when:
        AccountDTO accountDTO = accountMapper.mapAccountToDTO(account);

        then:
        accountDTO == new AccountDTO(
                java.util.UUID.fromString(accountId),
                "65 1090 1665 0000 0001 0373 7343",
                Money.of("123.45", "PLN")
        );
    }

    def "should create DTO from entity based on target currency"() {
        given:
        Currency targectCurrency = java.util.Currency.getInstance("EUR");
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744";
        AccountMapper accountMapper = new AccountMapperImpl(
                new ServiceConvertorImpl(
                        new CircuitBreakerImpl(new CurrencyConversionStubService()),
                        Currency.getInstance("PLN")
                )
        );
        Account account = new Account(
                Account.Id.of(accountId),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of("123.45", "PLN")
        )

        when:
        AccountDTO accountDTO = accountMapper.mapAccountToDTO(account, targectCurrency);

        then:
        accountDTO == new AccountDTO(
                java.util.UUID.fromString(accountId),
                "65 1090 1665 0000 0001 0373 7343",
                Money.of("27.16", "EUR")
        );
    }
}
