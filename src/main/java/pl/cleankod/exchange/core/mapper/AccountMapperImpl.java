package pl.cleankod.exchange.core.mapper;

import org.springframework.stereotype.Component;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.dto.AccountDTO;
import pl.cleankod.exchange.core.gateway.AccountMapper;
import pl.cleankod.exchange.core.gateway.ServiceConvertor;

import java.util.Currency;

@Component
public class AccountMapperImpl implements AccountMapper {

    private final ServiceConvertor serviceConvertor;

    public AccountMapperImpl(ServiceConvertor serviceConvertor) {
        this.serviceConvertor = serviceConvertor;
    }

    public AccountDTO mapAccountToDTO(Account account) {
        return new AccountDTO(account.id().value(), account.number().value(), account.balance());
    }

    public AccountDTO mapAccountToDTO(Account account, Currency targetCurrency) {
        return new AccountDTO(account.id().value(), account.number().value(), serviceConvertor.convert(account.balance(), targetCurrency));
    }
}
