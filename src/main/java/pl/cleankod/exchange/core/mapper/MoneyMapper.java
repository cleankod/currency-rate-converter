package pl.cleankod.exchange.core.mapper;

import org.mapstruct.Mapper;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.entrypoint.model.MoneyDto;

@Mapper(componentModel = "spring")
public interface MoneyMapper {
    MoneyDto moneyToMoneyDto(Money messageDTO);
}