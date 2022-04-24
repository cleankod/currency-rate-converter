package pl.cleankod.exchange

import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import pl.cleankod.BaseApplicationSpecification
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money

@Property(name = "provider.nbp-api.base-url", value = "http://api.nbp.pl/api")
class AccountIntegrationTest extends BaseApplicationSpecification {

    def "should return an account by ID with different currencies"(String currency, String amount) {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"

        when:
        Account response = get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        response == new Account(
                Account.Id.of(accountId),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of(amount, currency)
        )

        where:
        currency || amount
        "EUR"    || "27.27"
        "GBP"    || "22.95"
        "USD"    || "31.10"
    }

}
