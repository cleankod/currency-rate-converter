package pl.cleankod.exchange.serialization;

import com.fasterxml.jackson.annotation.JsonValue;

public class TestClasses {
    public static class RegularClass {}

    public record RecordClass() {}

    public record RecordClassWithJsonValue(@JsonValue String value) {

    }
    public record RecordClassWithMultipleJsonValues(@JsonValue String value1, @JsonValue int value2) {

    }
}
