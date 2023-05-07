# About
This is an example project that calculates the amount balance to a given currency. It is based on the [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) concept.

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
    "id": "fa07c538-8ce4-11ec-9ad5-4f5a625cd744",
    "number": "65 1090 1665 0000 0001 0373 7343"
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
* Better error handling, especially of potential errors from NBP API.
* Caching the NBP API results.
* Better logging with traceability.
* Replace exceptions with `Result` (`either`) which improves the overall methods API readability and forces error handling. Look into [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) as a starting point.
* Auto generating REST API docs.
* Integration tests with the real NBP API.
* The proposed architecture is not perfect. Suggest improvements. 
    * What is the meaning of globally configurable app.base-currency. looks like something specific to CurrencyConversionNbpService
    * It is not clear why it is possible to convert from PLN to USD, but it is not possible to convert from USD to PLN. We could just invert the exchange rate or use table C from NBP API.
    * `CurrencyConversionService` combines 3 responsibilities: retrieving exchange rate, conversion, and rounding. As in practice it is port to external system I would limit it to retrieving the exchange rate only.
