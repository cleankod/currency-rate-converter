package pl.cleankod.util.domain

import spock.lang.Specification

class ResultSuccessfulSpec extends Specification {
  def "should create successful result without value"() {
    when:
    def result = Result.successful()

    then:
    result.isSuccessful()
    !result.isFail()
  }

  def "should return given value"() {
    given:
    def value = "value"
    def result = Result.successful(value)

    when:
    def resultValue = result.successfulValue()

    then:
    resultValue == value
  }

  def "should throw exception when access to void value"() {
    given:
    Result<Void, Void> result = Result.successful()

    when:
    result.successfulValue()

    then:
    def exception = thrown(NullPointerException)
    exception.message == "The success value cannot be null."
  }

  def "should throw exception when result value is null"() {
    when:
    Result.successful(null)

    then:
    def exception = thrown(NullPointerException)
    exception.message == "The success value cannot be null."
  }
}
