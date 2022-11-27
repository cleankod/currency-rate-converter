package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.usecase.dto.AccountDto;

import java.util.Optional;

public interface AccountService {

    Optional<AccountDto> getAccountDtoById(String id, String currency);

    Optional<AccountDto> getAccountDtoByNumber(String accountNumber, String currency);
}
