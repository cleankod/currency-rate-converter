package pl.cleankod.util.domain

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class ResultEqualitySpec extends Specification {
  def "should verify equality"() {
    when:
    EqualsVerifier.forClass(Result).verify()

    then:
    noExceptionThrown()
  }
}
