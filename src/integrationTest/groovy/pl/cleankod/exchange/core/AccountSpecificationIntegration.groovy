package pl.cleankod.integration

import pl.cleankod.BaseApplicationIntegrationSpecification
import pl.cleankod.exchange.core.domain.Account

//TODO: add other tests
class AccountSpecificationIntegration extends BaseApplicationIntegrationSpecification {

    def "should return an account by ID with different currency"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        Account response = get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        response.id() == Account.Id.of(accountId) &&
        response.number() == Account.Number.of("65 1090 1665 0000 0001 0373 7343") &&
        response.balance().amount().doubleValue() > 0 &&
        response.balance().currency().currencyCode == "EUR"
    }

}
