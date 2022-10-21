package pl.cleankod.exchange.core.gateway;

import org.springframework.http.ResponseEntity;
import pl.cleankod.exchange.core.dto.AccountDTO;

public interface FinderService {

    ResponseEntity<AccountDTO> findAccountById(String id, String currency);
    ResponseEntity<AccountDTO> findAccountByNumber(String number, String currency);
}
