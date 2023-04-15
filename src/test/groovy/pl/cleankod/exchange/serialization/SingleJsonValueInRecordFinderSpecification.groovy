package pl.cleankod.exchange.serialization


import spock.lang.Specification

class SingleJsonValueInRecordFinderSpecification extends Specification {
    def "should throw for non record class"() {
        when:
        SingleJsonValueInRecordFinder.getAccessorFor(givenClass)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.endsWith("is not a record class.")

        where:
        givenClass << [TestClasses.RegularClass.class]
    }

    def "should throw if class does not contain exactly one JsonValue annotation"() {
        when:
        SingleJsonValueInRecordFinder.getAccessorFor(givenClass)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.endsWith("should contain exactly one data field with @JsonValue annotation.")

        where:
        givenClass << [TestClasses.RecordClass.class, TestClasses.RecordClassWithMultipleJsonValues.class]
    }

    def "should find accessor"() {
        when:
        def accessor = SingleJsonValueInRecordFinder.getAccessorFor(givenClass)

        then:
        accessor != null

        where:
        givenClass << [TestClasses.RecordClassWithJsonValue.class]
    }
}
