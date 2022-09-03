package pl.cleankod.exchange.entrypoint

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.gateway.AccountRepository
import pl.cleankod.exchange.core.gateway.CurrencyConversionService
import pl.cleankod.exchange.core.service.AccountService
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase
import pl.cleankod.exchange.core.usecase.FindAccountUseCase
import pl.cleankod.exchange.provider.AccountInMemoryRepository
import pl.cleankod.exchange.provider.CurrencyConversionNbpService
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest extends Specification {
    private MockMvc mockMvc;

    def setup(){
        AccountRepository accountRepository = new AccountInMemoryRepository()
        FindAccountUseCase accountUseCase = new FindAccountUseCase(accountRepository)
        CurrencyConversionService conversionService = new CurrencyConversionNbpService()
        Currency currency = Currency.getInstance("PLN")
        FindAccountAndConvertCurrencyUseCase accountAndConvertCurrencyUseCase = new FindAccountAndConvertCurrencyUseCase(accountRepository, conversionService, currency)
        AccountService accountService = new AccountService(accountAndConvertCurrencyUseCase, accountUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService)).build();
    }

    def "should return 200 and account by id"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"

        when:
        def response = mockMvc.perform(get("/accounts/${accountId}")).andReturn().response

        then:
        response.status == 200
        response.contentAsString ==  new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(new Account(
                Account.Id.of(accountId),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of("123.45", "PLN")))
    }

    def "should return 200 and account by number"() {
        given:
        def number = "75 1240 2034 1111 0000 0306 8582"

        when:
        def response = mockMvc.perform(get("/accounts/number=${number}")).andReturn().response

        then:
        response.status == 200
        response.contentAsString ==  new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(new Account(
                Account.Id.of("78743420-8ce9-11ec-b0d0-57b77255c208"),
                Account.Number.of(number),
                Money.of("456.78", "EUR")))
    }

}
