package pl.cleankod.exchange.core.domain.entrypoint

import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase
import pl.cleankod.exchange.core.usecase.FindAccountUseCase
import pl.cleankod.exchange.entrypoint.AccountService
import spock.lang.Specification

class AccountServiceSpecification extends Specification {

    private def findAccountAndConvertCurrencyUserCase = Mock(FindAccountAndConvertCurrencyUseCase.class)
    private def findAccountUseCase = Mock(FindAccountUseCase.class)
    private def accountService = new AccountService(findAccountAndConvertCurrencyUserCase, findAccountUseCase)

    private static def EXISTING_ACCOUNT = new Account(
            Account.Id.of("fa07c538-8ce4-11ec-9ad5-4f5a625cd744"),
            Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
            Money.of("123.45", "PLN"))

    def 'should get account by id and currency'() {
        given:
        def id = Account.Id.of('fa07c538-8ce4-11ec-9ad5-4f5a625cd744')
        def currency = Currency.getInstance('EUR')
        findAccountAndConvertCurrencyUserCase.execute(id, currency) >> Optional.of(EXISTING_ACCOUNT)

        when:
        def account = accountService.findAccountByIdAndCurrency(id, currency)

        then:
        account.isPresent()
        account.get().id() == id
    }

    def 'should get account by id'() {
        given:
        def id = Account.Id.of('fa07c538-8ce4-11ec-9ad5-4f5a625cd744')
        findAccountUseCase.execute(id) >> Optional.of(EXISTING_ACCOUNT)

        when:
        def account = accountService.findAccountByIdAndCurrency(id, null)

        then:
        account.isPresent()
        account.get().id() == id
    }
}
