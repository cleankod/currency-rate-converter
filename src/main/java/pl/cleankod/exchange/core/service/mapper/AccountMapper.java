package pl.cleankod.exchange.core.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.model.AccountDto;

@Mapper(uses = { MoneyMapper.class }, componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "messageDTO.id.value", target = "id")
    @Mapping(source = "messageDTO.number.value", target = "number")
    AccountDto accountToAccountDto(Account messageDTO);
}