![branches coverage](.github/badges/branches.svg)
![jacoco analysis](.github/badges/jacoco.svg)

[![Gradle Package](https://github.com/evatikiotis/currency-rate-converter/actions/workflows/gradle-package.yml/badge.svg?branch=feature%2Fcontinous-integration-CI&event=push)](https://github.com/evatikiotis/currency-rate-converter/actions/workflows/gradle-package.yml)

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
* Investigate whether it is possible to implement the value-object serialization, to avoid `value` nested field in JSON.
  See [#10](https://github.com/cleankod/currency-rate-converter/pull/10) as a starting point.
    * ✓ contributed on existing branch by Fixing the tests.
* Move parameter-specific logic outside the controller.
* Replace exceptions with `Result` (`either`) which improves the overall methods API readability and forces error
  handling. Look into [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) as a
  starting point.
* Better error handling, especially of potential errors from NBP API.
    * ✓ created custom exception and improved controller advice
* Caching the NBP API results.
* Circuit-breaker for the NBP API client.
    * ✓ but needs load testing and would be nice to configure Hystrix Dashboard
* Better logging with traceability.
* Test coverage report.
    * ✓ using jacoco
* Auto generating REST API docs.
    * ✓ using OpenAPI
* Integration tests with the real NBP API.
    * 𝙭 Faced issues supporting test profiles
* Replace Spring Framework with a different one.
* The proposed architecture is not perfect. Suggest improvements.
* CI
    * ✓ Fist Milestone is reached. The implemented CI includes:
        * Publishing jar artifact
        * Generate test and test coverage reports and GitHub badges
        * Build and push docker image in ghcr.io registry

# Necessary Adjustments and Personal Touch

* Included org.springframework.boot plugin to use bootJar gradle task and create a proper artifact. Otherwise
  application fails to start with ServletWebServerFactory
* Implemented gradle versioning tasks that need to be consumed by the CI solution and create SNAPSHOT/RELEASE/DEV
  artifact
* Parallel test execution config

# Improvement Proposals

* Remove method convert from Money
* Dynamic ports for wiremock and SpringApplication during tests
* Implement Profiles for application and tests and support dynamic config
* Hystrix Implementation needs Load testing
* Implement Profiling 
