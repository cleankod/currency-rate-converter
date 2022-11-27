package pl.cleankod.util.domain

import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Function

class ResultMappersSpec extends Specification {

  private Function<String, Boolean> successMapper = {
    it.matches("success")
  }
  private Function<String, Integer> failureMapper = { it.size() }

  @Unroll
  def "should fold with proper mapper when result success is #result.successful"() {
    when:
    def actualResultFoldValue = result.fold(successMapper, failureMapper)

    then:
    actualResultFoldValue == expectedResultValue

    where:
    result                       || expectedResultValue
    Result.fail("size-6")        || 6
    Result.successful("success") || Boolean.TRUE
  }

  @Unroll
  def "should biMap with proper mapper when result success is #result.successful"() {
    when:
    def actualMappedResult = result.biMap(successMapper, failureMapper)

    then:
    actualMappedResult.isSuccessful() == isSuccessfull
    actualMappedResult.isFail() == isFail
    actualMappedResult.successfulValue == sucessfulValue
    actualMappedResult.failValue == failValue

    where:
    result                       || isSuccessfull || isFail        || sucessfulValue || failValue
    Result.fail("size-6")        || Boolean.FALSE || Boolean.TRUE  || null           || 6
    Result.successful("success") || Boolean.TRUE  || Boolean.FALSE || Boolean.TRUE   || null
  }

  @Unroll
  def "should map twice either success or failure with proper mapper when result success is #result.successful"() {
    given:
    Function<Boolean, Integer> anotherSuccessMapper = {
      it == Boolean.TRUE ? 1 : 0
    }

    Function<Integer, Short> anotherFailMapper = {
      it.shortValue()
    }

    when:
    def actualMappedResult = result
        .successMap(successMapper)
        .successMap(anotherSuccessMapper)
        .failMap(failureMapper)
        .failMap(anotherFailMapper)

    then:
    actualMappedResult.isSuccessful() == isSuccessfull
    actualMappedResult.isFail() == isFail
    actualMappedResult.successfulValue == sucessfulValue
    actualMappedResult.failValue == failValue

    where:
    result                       || isSuccessfull || isFail        || sucessfulValue || failValue
    Result.fail("size-6")        || Boolean.FALSE || Boolean.TRUE  || null           || 6 as short
    Result.successful("success") || Boolean.TRUE  || Boolean.FALSE || 1              || null
  }

  @Unroll
  def "should get successful value or map failure as a fallback when result success is #result.successful"() {
    given:
    Function<String, String> successMapper = {
      it == "success" ? "TRUE" : "FALSE"
    }
    Function<String, String> failureMapper = { it.size().toString() }

    when:
    String actualResultValue = result.successMap(successMapper)
        .orElseGet(failureMapper)

    then:
    actualResultValue == expectedResultValue

    where:
    result                       || expectedResultValue
    Result.fail("size-6")        || "6"
    Result.successful("success") || "TRUE"
  }

  @Unroll
  def "should get successful value or alternative value as a fallback when result success is #result.successful"() {
    given:
    Function<String, String> successMapper = {
      it == "success" ? "TRUE" : "FALSE"
    }
    String alternativeValue = "alternative-value"

    when:
    String actualResultValue = result.successMap(successMapper)
        .orElse(alternativeValue)

    then:
    actualResultValue == expectedResultValue

    where:
    result                       || expectedResultValue
    Result.fail("failure")       || "alternative-value"
    Result.successful("success") || "TRUE"
  }

  @Unroll
  def "should be able to map failure when success type is Void"() {
    given:
    Function<String, String> failureMapper = { it.size().toString() }

    when:
    Result<Void, String> actualResult = result.failMap(failureMapper)

    then:
    actualResult.isSuccessful() == expectedIsSuccessful
    actualResult.successfulValue == expectedSuccessfulValue
    actualResult.failValue == expectedFailValue

    where:
    result                                || expectedIsSuccessful || expectedSuccessfulValue || expectedFailValue
    Result.<Void, String> fail("failure") || false                || null                    || '7'
    Result.<Void, String> successful()    || true                 || null                    || null
  }
}
