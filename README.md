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
* Investigate whether it is possible to implement the value-object serialization, to avoid `value` nested field in JSON. See [#10](https://github.com/cleankod/currency-rate-converter/pull/10) as a starting point. ✔
  (Added an example how solve the issue using jackson mixins, in test ObjectMapperSerializationSpecification)
* Move parameter-specific logic outside the controller.
* Replace exceptions with `Result` (`either`) which improves the overall methods API readability and forces error handling. Look into [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) as a starting point.
* Better error handling, especially of potential errors from NBP API.
* Caching the NBP API results. ✔
* Circuit-breaker for the NBP API client. ✔
  Added Hystrix implementation of the client Feign with fallback
* Better logging with traceability. ✔ Added logback and generated request id
* Test coverage report. ✔ (Added plugin jacoco, report can be found in the folder build/reports/jacoco)
* Auto generating REST API docs. ✔
  (The openapi specification is available after application start on address http://localhost:8080/swagger/currency-rate-converter-0.1.yml)
* Integration tests with the real NBP API. ✔
* Replace Spring Framework with a different one. ✔️
  (Replaced with micronaut framework.)
* The proposed architecture is not perfect. Suggest improvements. ✔

## Suggested improvements

* Classes FindAccountAndConvertCurrencyUseCase and FindAccountUseCase should be renamed to not contain verb in the name.
* Class Money doesn't need method convert.
* Separate the conversion service and NBP client