package pl.cleankod.exchange.adapter.entrypoint.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.cleankod.exchange.adapter.entrypoint.model.AccountDto;
import pl.cleankod.exchange.adapter.entrypoint.model.MoneyDto;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;

@Mapper
public abstract class DtoMapperV1 {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "number", source = "number.value")
    public abstract AccountDto mapToAccountDto(Account account);

    public abstract MoneyDto mapToMoneyDto(Money money);

}
