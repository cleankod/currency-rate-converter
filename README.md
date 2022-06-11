# About
This is an example project that calculates the amount balance to a given currency.

# Requirements
* JDK 17
* Gradle 7.4 (you can use the gradle wrapper instead)

# REST API
## Get account
Endpoints:
* `GET /accounts/{id}`
* `GET /accounts/number={number}`

Parameters:
* `currency` (not required) - calculate the account balance based on the today's average currency rate.

Sample request by ID:
```
http://localhost:8080/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=EUR
```

Sample request by account number:
```
http://localhost:8080/accounts/number=65+1090+1665+0000+0001+0373+7343?currency=PLN
```

Will produce:
```json
{
    "balance": {
        "amount": 27.27,
        "currency": "EUR"
    },
    "id": {
        "value": "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
    },
    "number": {
        "value": "65 1090 1665 0000 0001 0373 7343"
    }
}
```

# Assumptions and design decisions
## Black-box testing
Black-box testing is mostly used in order to favor refactoring. It is much simpler to completely change the underlying
implementation of a use case without changing the tests.

## Framework-less tests
Only the `BaseApplicationSpecification` contains library-specific code but no framework-specific initialization.
This approach eases the migration to other potential framework or toolset. The whole specification for the project
stays the same.

## Framework-less modules' core
Wherever possible, no Framework-specific or library-specific stuff was used inside the actual modules' core.
This also eases potential framework change or upgrade. The framework upgrade could also be more seamless for all
of those changes that are not backwards compatible because framework specific stuff is kept in one place and the
business logic is not polluted.

## Value-objects
There is no simple value passed around in the project. Every business value is encapsulated within a value-object.
It increases readability, enables nice methods override
(instead of: `findAccountById(String id)`, `findAccountByNumber(String accountNumber)`,
you can use: `find(Account.Id id)`, `find(Account.Number number)`), encapsulates internal data type.

Also, value-objects are responsible for a little more than just plain data holding.

# To do
* Rounding when calculating the amount.
* Investigate whether it is possible to implement the value-object serialization, to avoid `value` nested field in JSON. See [#10](https://github.com/cleankod/currency-rate-converter/pull/10) as a starting point.
* Move parameter-specific logic outside the controller.
* Replace exceptions with `Result` (`either`) which improves the overall methods API readability and forces error handling. Look into [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) as a starting point.
* Better error handling, especially of potential errors from NBP API.
* Caching the NBP API results.
* Circuit-breaker for the NBP API client.
* Better logging with traceability.
* Test coverage report.
* Auto generating REST API docs.
* Integration tests with the real NBP API.
* Replace Spring Framework with a different one.
* The proposed architecture is not perfect. Suggest improvements.

# Undone:

* Replace Spring Framework with a different one.
* Replace exceptions with `Result` (`either`) which improves the overall methods API readability and forces error
  handling. Look into [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) as a
  starting point.

# Suggestions:

* Move bean declarations out of ApplicationInitialize.
* Maybe I'm missing something, but bullet point "Rounding when calculating the amount" looks like it's resolved
* I would remove static methods in Money - I don't see real reason of having them
* I would add archunit library to make sure that syntax in CurrencyConversionNbpService.getMidRate and
  CurrencyConversionNbpService.fallback is the same
* I would add at least one e2e test with happy path
* I would suggest to add more implementation of CurrencyConversionService, because currently it's possible only to
  convert currencies from PLN to others.

#### Api documentation: [swagger] (http://localhost:8080/swagger-ui/index.html)

#### Test coverage report: in gradle.verification task, please run jococoTestReport
