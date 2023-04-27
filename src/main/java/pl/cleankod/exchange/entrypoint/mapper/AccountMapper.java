package pl.cleankod.exchange.entrypoint.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.entrypoint.model.AccountWrapper;
import pl.cleankod.exchange.entrypoint.model.MoneyWrapper;

@Mapper
public abstract class AccountMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "number", source = "number.value")
    public abstract AccountWrapper mapToAccountWrapper(Account account);

    public abstract MoneyWrapper mapToMoneyWrapper(Money money);
}