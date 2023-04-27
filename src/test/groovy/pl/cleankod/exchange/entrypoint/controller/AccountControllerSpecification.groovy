package pl.cleankod.exchange.entrypoint.controller

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.gateway.AccountRepository
import pl.cleankod.exchange.core.gateway.CurrencyConversionService
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase
import pl.cleankod.exchange.core.usecase.FindAccountUseCase
import pl.cleankod.exchange.entrypoint.mapper.AccountMapper
import pl.cleankod.exchange.entrypoint.service.AccountService
import pl.cleankod.exchange.entrypoint.service.AccountServiceImpl
import pl.cleankod.exchange.provider.nbp.repository.AccountInMemoryRepository
import pl.cleankod.exchange.provider.nbp.service.CurrencyConversionNbpService
import pl.cleankod.exchange.provider.nbp.service.ExchangeRatesNbpClientImpl
import spock.lang.Specification
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@WebMvcTest(controllers = [AccountController])
@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountControllerSpecification extends Specification {

    @Autowired
    AccountServiceImpl accountService

    @Autowired
    ExchangeRatesNbpClientImpl exchangeRatesNbpClient

    protected MockMvc mvc

    def mapper = Mock(AccountMapper)

    def setup() {
        AccountRepository accountRepository = new AccountInMemoryRepository()
        FindAccountUseCase accountUseCase = new FindAccountUseCase(accountRepository)
        CurrencyConversionService conversionService = new CurrencyConversionNbpService(exchangeRatesNbpClient)
        Currency currency = Currency.getInstance("PLN")
        FindAccountAndConvertCurrencyUseCase accountAndConvertCurrencyUseCase = new FindAccountAndConvertCurrencyUseCase(accountRepository, conversionService, currency)
        AccountService accountService = new AccountServiceImpl(accountAndConvertCurrencyUseCase, accountUseCase)
        mvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService, mapper)).build()

    }

    def "should return correct account by id"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"

        and:
        accountService.findAccountById(accountId) >> Optional.of(
                new Account(
                        Account.Id.of(accountId),
                        Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                        Money.of("123.45", "PLN")))

        when:
        def response = mvc.perform(get("/accounts/${accountId}")).andReturn().response

        then:
        response.status == 200
    }

    def "should return correct account by number"() {
        given:
        def number = "75 1240 2034 1111 0000 0306 8582"

        when:
        def response = mvc.perform(get("/accounts/number=${number}")).andReturn().response

        then:
        response.status == 200

    }

}
