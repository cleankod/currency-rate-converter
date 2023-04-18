package pl.cleankod.exchange.serialization;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Finds single field annotated with @JsonValue annotation for record classes.
 * Purpose of this class is to mimic what will happen in Jackson library
 * when it will be able to handle record classes properly.
 */
public class SingleJsonValueInRecordFinder<T> {

    private final RecordComponent recordComponent;

    public SingleJsonValueInRecordFinder(Class<T> recordClass) {
        if (!recordClass.isRecord()) {
            throw new IllegalArgumentException(
                String.format("Provided class '%s' is not a record class.", recordClass.getCanonicalName())
            );
        }

        List<RecordComponent> componentsWithJsonCreator = Arrays.stream(recordClass.getRecordComponents())
                .filter(recordComponent -> recordComponent.getAccessor().isAnnotationPresent(JsonValue.class))
                .toList();

        if (componentsWithJsonCreator.size() != 1) {
            throw new IllegalArgumentException(
                String.format(
                    "Provided class '%s' should contain exactly one data field with @JsonValue annotation.",
                    recordClass.getCanonicalName()
                )
            );
        }
        this.recordComponent = componentsWithJsonCreator.get(0);
    }

    private Function<T, Object> wrapAccessorFunction(RecordComponent recordComponent) {
        return param -> {
            try {
                return recordComponent.getAccessor().invoke(param);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    public Function<T, Object> getAccessor() {
        return wrapAccessorFunction(recordComponent);
    }

    public static <T> Function<T, Object> getAccessorFor(Class<T> recordClass) {
        return new SingleJsonValueInRecordFinder<>(recordClass).getAccessor();
    }
}
