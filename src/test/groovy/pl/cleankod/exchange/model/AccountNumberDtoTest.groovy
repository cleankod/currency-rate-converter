package pl.cleankod.exchange.model

import pl.cleankod.exchange.entrypoint.model.AccountNumberDto
import spock.lang.Specification

class AccountNumberDtoTest extends Specification {

    def "should replace + with empty spaces"() {
        given:
        AccountNumberDto number = new AccountNumberDto("1234+4567+7890")

        when:
        String result = number.decode()

        then:
        result == "1234 4567 7890"
    }
}
