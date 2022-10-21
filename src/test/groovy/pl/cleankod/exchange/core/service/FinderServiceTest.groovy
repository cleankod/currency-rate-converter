package pl.cleankod.exchange.core.service

import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.gateway.AccountRepository
import pl.cleankod.exchange.core.gateway.FinderService
import pl.cleankod.exchange.core.mapper.AccountMapperImpl
import pl.cleankod.exchange.core.usecase.CurrencyConversionException
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase
import pl.cleankod.exchange.core.usecase.FindAccountUseCase
import spock.lang.Specification

class FinderServiceTest extends Specification {

    def "should throw exception for a non-existent currency"() {
        when:
        FinderService finderService = new FinderServiceImpl(
                new FindAccountAndConvertCurrencyUseCase(new AccountRepository() {
                    @Override
                    Optional<Account> find(Account.Id id) {
                        return null
                    }

                    @Override
                    Optional<Account> find(Account.Number number) {
                        return null
                    }
                }, new AccountMapperImpl(null)),
                new FindAccountUseCase(new AccountRepository() {
                    @Override
                    Optional<Account> find(Account.Id id) {
                        return null
                    }

                    @Override
                    Optional<Account> find(Account.Number number) {
                        return null
                    }
                }, new AccountMapperImpl(null))
        );

        finderService.findAccountById("id", "");

        then:
        thrown(CurrencyConversionException)

        where:
        givenCurrency << ["ZZZ", "123.45", "---", "000", "A"]
    }
}
