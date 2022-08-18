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
* Rounding when calculating the amount is not done correctly for this type of operation.
  * This one was very interesting. First it showed me that the numeric operations must be taken out of the conversion 
  service, so they will be unit tested easily regardless of the currencies.
  * Second - I probably would have gone with the Money API which was added in Javax a while ago to not care about the 
  money conversion code. I am not completely sure that this solution is 100% correct. It looks like it as far as I can 
  see and from the tests done. The obvious thing which made the rounding errors appear in the first place was that the 
  given amount did not have a scale set. But my solution will work perfect only for the money which have the subunit with 
  2 decimals (cent / eurocent / ban / etc). I think that for the other types of currencies the scale must match the number
  of subdivisions, case in which the rounding errors might return.
  * For the purpose of this process I would say is a good enough solution, I will not change now the Money with the javax Money but in a real project I think that would be the best option 
* 
* `Value-object serialization`
* Investigate whether it is possible to implement the value-object serialization, to avoid `value` nested field in JSON. 
See [#10](https://github.com/cleankod/currency-rate-converter/pull/10) as a starting point. Or maybe there is a better solution to the problem at hand?
  * Commit 2
  * Yes, it is possible but I prefer a different approach in which the domain object are not tainted by serialization code
  * That is why I fixed it with a simple serializer for each type with a value and it works like a charm (and the only change needed is in the app initializer)
  * Did not like the changes needed to make the tests pass, it was a little tedious, however it works even though might be a cleaner way
* 
* Move parameter-specific logic outside the controller.
* 
* 
* Better error handling, especially of potential errors from NBP API.
* 
* Caching the NBP API results.
* 
* Circuit-breaker for the NBP API client.
* 
* Better logging with traceability.
* 
* Replace exceptions with `Result` (`either`) which improves the overall methods API readability and forces error handling. Look into [cleankod/architecture-archetype](https://github.com/cleankod/architecture-archetype) as a starting point.
* 
* `Test coverage report.`
  * Commit 3 
  * Ok, this one was easy altough first I wanted to skip it - just added the sample gradle config from here https://docs.gradle.org/current/userguide/jacoco_plugin.html
  * Not too bad coverage, there is one extra class not covered (the stub) which I wanted to exclude
  * The first two exclusions trials were not succesful (one advice was for an older version of gradle and the second way 
  did not worked). Normally I keep on trying but I do not think it matters that much at this stage of the process
* 
* `Auto generating REST API docs.`
  * Commit 5 (just test - no implementation at this moment) 
  * I would go with Swagger for this task because I like how the UI looks and that we can set it up so the API can actually be tested.
  * This is a very promising tutorial, I would follow it to finish the task, but I will see if I will have time after I do a few more interesting parts
  * https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
* 
* `Integration tests with the real NBP API.`
* 
* `Replace Spring Framework with a different one.`
  * Commit 4 (just text - no implementation at this moment)
  * I honestly to not plan to do this - is not very difficult from this point as besides the initializer where 
  everything is wired there is no explicit dependency on the framework itself and will try to keep it in this way. 
  * I know other todos which I might do soon can be solved by throwing an annotation like @Cacheable but will try to 
  keep the current state of affairs in order to keep an eventual migration easy
  * If I would actually do it I would replace Spring with Quarkus because that is a framework which I really wanted 
  to try for a while now because it seems it has a few nice advantages over Spring. 
  * Also from what I read might be very easy to replace as they build it with this Spring compatibility in mind. 
  I will see how busy will be in the next days maybe I will take a chance at this later.
* 
* `The proposed architecture is not perfect. Suggest improvements.`
  * Commit 1.
  * My first step was to understand what is this application doing. After running the tests and the application and applying some
  refactoring in some parts of the code which I had troubles understanding I finally realized. The application converts the amount
  of money in an account to the currency specified as a parameter FROM PLN. 
  * When I finally realized this I also found an interesting bug, an account defined with a monetary value expressed in EUR for example
  will always convert that amount in a foreign currency as if the account would be expressed in PLN. (can be tried on the account 78743420-8ce9-11ec-b0d0-57b77255c208
  with the currency param USD - the returned amount in USD will not represent those 456.78 euros but 456.78 PLN). Of course this is wrong, so I fixed
  the behaviour. Once the bug was fixed I extracted this abstraction called FixedCurrencyConversionService (working name) which has the 
  responsibility of converting the monetary amount received into a foreign currency from the base currency. 
  * And from this point I can provide the suggested architectural improvement: As we can see only the conversion from PLN to any other currency is supported, 
  and we could have other implementations which converts RON to any other currency, or EUR to any other currency, etc. 
  * In order to achieve this I created a CurrencyConversionServiceProvider which will return the appropriate FixedCurrencyConversionService based on the
  currency of the given Money. All the combinations which are invalid (not implemented yet, for example a conversion from EUR to USD) will simply return
  an empty value which the use case can handle as wishes, for example throwing the CurrencyConversionException as it is now. 
  * If we want to extend the service with new combinations we can simply create clients and subclasses for FixedCurrencyConversionService and then wire
  them up in the CurrencyConversionServiceProvider. In this way we can extend the service with newer combination without any change to the existing 
  implementation.
