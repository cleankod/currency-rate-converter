package pl.cleankod.exchange.entrypoint

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class ObjectMapperSpecification extends Specification {
    ObjectMapper objectMapper

    def setup() {
        objectMapper = new ObjectMapperFactory().objectMapper()
    }

    def "should inline SingleValueObject during serialization"() {
        when:
        def actual = objectMapper.writeValueAsString(givenObject)

        then:
        actual == expected

        where:
        expected                                 | givenObject
        '"abc"'                                  | new StringWrapper(("abc"))
        '2'                                      | new IntWrapper(2)
        '"3874ec97-088a-406a-8bfb-04c61e4f578f"' | new UuidWrapper(UUID.fromString("3874ec97-088a-406a-8bfb-04c61e4f578f"))
        'null'                                   | null
        '{"inner":"text"}'                       | new StringWrapperWrapper(new StringWrapper("text"))
    }

    def "should deserialize inlined SingleValueObject"() {
        when:
        def actual = objectMapper.readValue(givenJson, givenType)

        then:
        actual == expected

        where:
        givenJson                                | givenType            | expected
        '"abc"'                                  | StringWrapper        | new StringWrapper("abc")
        '100'                                    | IntWrapper           | new IntWrapper(100)
        '"3874ec97-088a-406a-8bfb-04c61e4f578f"' | UuidWrapper          | new UuidWrapper(UUID.fromString("3874ec97-088a-406a-8bfb-04c61e4f578f"))
        'null'                                   | StringWrapper        | null
        '{"inner":"text"}'                       | StringWrapperWrapper | new StringWrapperWrapper(new StringWrapper("text"))
    }
}


