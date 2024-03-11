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
* Rounding when calculating the amount is not done correctly for this type of operation (we're loosing money!) and it is done in the wrong place.
  - I removed the business logic from Money.class (the code calling the conversion function was moved to FindAccountAndConvertCurrencyUseCase). 
  Also, the  RoundingMode.HALF_EVEN was used because it's known to  be used for financial roundings
* Investigate whether it is possible to implement the value-object serialization, to avoid `value` nested field in JSON. See [#10](https://github.com/cleankod/currency-rate-converter/pull/10) as a starting point. Or maybe there is a better solution to the problem at hand?
   - I added  @JsonCreator and @JsonValue so the serialisation and deserialisation are done without the nested "value" object
* Move parameter-specific logic outside the controller.
   - The logic was moved to AccountService
* Better error handling, especially of potential errors from NBP API.
   - I added NbpApiErrorDecoder and specific exception classes to handle potential errors coming from NBP Api
* Caching the NBP API results.
   - I used Caffeine for this part mainly because it's a standalone caching library with no other dependencies, we can keep our core modules free from any framework-specific or 
    library-specific code, so we can easily migrate to other frameworks in the future. 
* Circuit-breaker for the NBP API client.
   - I used Resilience4J for circuit breaker implementation with mockup configuration in application.properties. It will retry for 3 times, it will wait for 200ms and in case of failure it will throw FeignException
* Better logging with traceability.
   - I used slf4j and logged DEBUG, INFO and ERROR usecases throughout the entire flow 
* Replace exceptions with `Result` (`either`) which improves the overall methods API readability and forces error handling. Look into [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) as a starting point.
   - Exceptions are now wrapped in a Failure object. Failure type is part of Result type. 
    Now a method will pass a result which depending on the behaviour will contain either a value (the successful case) or a failure (eg.: one potential failure may be Account not found or Currency conversion exception; Failure types are defined in Failures.class)
    The code is now changed to handle Results containing either a value or a Failure.
* Test coverage report.
   - Jacoco was used for coverage report. The build script was changed so the report is now generated in {buildDir}/reports/jacoco
* Auto generating REST API docs.
   - springdoc-openapi was added and swagger UI is available at http://localhost:8080/swagger-ui/index.html
* Integration tests with the real NBP API.
* Replace Spring Framework with a different one.
   - I was able to replace Spring with micronaut, it is not included in this pull request because it introduces more complexity but it can be shown locally or in a separate pull request
* The proposed architecture is not perfect. Suggest improvements.
   - The proposed architecture is not bad, it's just a flavour which if I would choose to change, I would go with onion architecture.

