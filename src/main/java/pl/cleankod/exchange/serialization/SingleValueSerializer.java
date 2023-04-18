package pl.cleankod.exchange.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.function.Function;

public class SingleValueSerializer<T1, T2> extends JsonSerializer<T1> {

    private final Class<T1> handledType;
    private final Function<T1, T2> valueProvider;

    @Override
    public Class<T1> handledType() {
        return handledType;
    }

    public SingleValueSerializer(Class<T1> handledType, Function<T1, T2> valueProvider) {
        this.handledType = handledType;
        this.valueProvider = valueProvider;
    }
    @Override
    public void serialize(T1 value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(valueProvider.apply(value));
    }
}
