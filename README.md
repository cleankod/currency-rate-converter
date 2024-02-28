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
Rounding when calculating the amount is not done correctly for this type of operation (we're loosing money!) and it is done in the wrong place.
--removed the conversion logic from the Money class, this is just a model class so no point in storing logic in it
-- i just call the conversionService from the Account Service,it is also easier to write unit test and mock the services instead of a model class
--for the conversion part from my tests i think it's losing some precision due to the scale so i added a larger scale(what if we want more precision add a even larger scale?),also regarding rounding i have seen that the proposed solution
-- from the java DOCs is to use HALF_EVEN, in a real system i will discuss with my colleagues do more testing and also more investigation

Investigate whether it is possible to implement the value-object serialization, to avoid value nested field in JSON. See #10 as a starting point. Or maybe there is a better solution to the problem at hand?
--i took a look at the suggested implementation, from my point of view to add logic to a serializer(often developers are not even aware of it) seems a overkill especeially for a simple object
-- also i view that Account object as the database representation of the table ,maybe it would be a better idea to have a dto like AccountDTO and the fields that match our use case
--another solution maybe it could be to use graphql for controlling the output, just an ideea, i didn't work with it until now but we can discuss about it

Move parameter-specific logic outside the controller.
--moved the logic to the AccountService class
--i put the common logic in a private method and the public one handle the retrieval of the Account and mapping of the accountNumber
--from my point of view the REST methods are doing 2 things ,either execute a conversion or a simple findby,maybe we should have separate methods but we can discuss about it


Better error handling, especially of potential errors from NBP API.
--made use of the existing exception handler
--a next step can be to create a custom exception something like NbpAPIException(HTTPSTATUSCODE, message, originalExpcetion) extends FeignException and throw our custom Exception
--for the exception handler we must make sure we don't send sensitive informative back to the user, exception should be logged and the user receives a status code,
--a error message without senstivie info

Caching the NBP API results.
--added spring @Cacheale to cache the results of nbp api, also evict the data at some interval
--maybe we can store the eviction interval in the application.properties

Circuit-breaker for the NBP API client.
-- added @Retryable to stop  calling nbp api if for some reason it fails a couple of times in a row
--further maybe we can add some method to be called if the nbp api fails, for example we may want to store the failed queries in a table
-- the annotation is commented because for some reason intellij does not see the depedency, anyway we can discuss about the approach

Better logging with traceability.
-- added some logs for caching and one in the exception handler but from my point of view we should add an aspect to have this logging logic in one place
--for traceability if we want to track each request and maybe interractions with other systems we need a unique id per request(a traceId , i think this is how it was called in Datadog)
--we can try to setup Sleuth(to generate the traceId or correleationId and monitor logs inside the application) and Zipkin to aggregate the logs and see all the interactions with external services
-- this https://medium.com/@bubu.tripathy/distributed-tracing-with-spring-cloud-sleuth-and-zipkin-9106c8afd349 seems promicing
--i had a project with Sleuth,zipkin, datadog configured, but i don't remember doing it myself, anyway it was really easy to check the logs and debug

Replace exceptions with Result (either) which improves the overall methods API readability and forces error handling. Look into cleankod/architecture-archetype as a starting point.
--not sure that i understand this ,but we can discuss about it

Test coverage report.
--i added jacoco for coverage and it generates a nice report , i see that CurrencyConversionStubService has 0% , this is class is not even used maybe it makes sense to exclude it
--usually i exclued config classes , another improvement can be to add sonarqube to check code quality
--in previous projects i had sonarqube and in a sprint some person worked 2 3 days to fix the more urgent or critical issues, and little by little we can end up fixing most of the important issues

Auto generating REST API docs.
--for this i would suggest Swagger , it is easy to setup has a nice and easy to understood UI, it can be configured to support authentication BasicAuth etc
-- i did not manage to get the gradle dependecines for it, i really have no ideea why...

Integration tests with the real NBP API.
--not sure why we should have an integration which should call the real API, what happens if that service is unavaible for some time,the pipeline will fail because the test based on the external service fails
--so the deployment fails? correct me if i am wrong, i was thinking to add mock server and mock the call to NBP API but it does look like it's already done in AccountSpecification,we can discuss more about it

Replace Spring Framework with a different one.
--at fist i did not quite understood the question but now i think i got it
--so all the spring stuff is done in one class ApplicationInitializer, i guess this makes changing the spring depedency container with something else easier
--but a few questions remain, what about the @RestController,@Cacheacle ,@Retryable
--also we need a framework that supports MVC, and also is able to output a jar with embedde tomcat as Spring does otherwise it may impact how the deployment is done
--maybe a candidate for this could be Quarkus,never tried it before, i know it is a more lightweight alternative to spring
--also what about spring data, can we find something simillar in Quarkus or we have to mannualy write hibernate code :)

The proposed architecture is not perfect. Suggest improvements.
--maybe avoid model classes with logic in them , makes them hard to test
--i see that the CurrencyConversionStubService it's supposed to support multiple exchange types, i left a brief ideaa of how i would do it
--the code seems a little difficult to understand, i had to spend quite some time understanding what the app does,renaming some of the classes helps, for example the UseCase ones, with something more suggestive
-- the code inside the conversion service was so difficult to understand even if there are just a few lines, it's very hard to figure out that the NBP API gives back a coversion rate between targetCurrency and PLN(renaming the variables definely helps )
--and then we must apply another conversion to get the correct amount , imagine converting from an EUR Account to USD currenct, i left a couple of comments
--i know that i used the stubCurrencyConversion class maybe not a great ideea we can discuss it, also this Stub class must definetelety be renamed
