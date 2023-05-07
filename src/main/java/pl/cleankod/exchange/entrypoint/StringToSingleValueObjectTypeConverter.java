package pl.cleankod.exchange.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.TypeConverter;
import jakarta.inject.Singleton;
import pl.cleankod.exchange.core.domain.SingleValueObject;

import java.util.Optional;

@Singleton
class StringToSingleValueObjectTypeConverter implements TypeConverter<String, SingleValueObject<?>> {
    private final ObjectMapper objectMapper;

    StringToSingleValueObjectTypeConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<SingleValueObject<?>> convert(String object, Class<SingleValueObject<?>> targetType, ConversionContext context) {
        TextNode textNode = TextNode.valueOf(object);
        try {
            SingleValueObject<?> value = objectMapper.treeToValue(textNode, targetType);
            return Optional.of(value);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
