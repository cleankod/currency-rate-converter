package pl.cleankod.exchange.entrypoint;

import pl.cleankod.exchange.core.domain.SingleValueObject;

import java.util.UUID;

record StringWrapper(String value) implements SingleValueObject<String> {
}

record IntWrapper(Integer value) implements SingleValueObject<Integer> {
}

record UuidWrapper(UUID value) implements SingleValueObject<UUID> {
}

record StringWrapperWrapper(StringWrapper inner) {
}
