# Prerequisites
- Java 11 (Probably won't work with Java8)
- Maven
- Internet connection

# Architecture decisions
- Event Sourcing and CQRS 
- RestFull API
- Modular monolith with modularization on package level. That provides high cohesion, low coupling
  and great options for a journey to microservices.

# Build and Run
##There are two options to build and run the application:
- using video-store.sh script
    *`video-store.sh -build`
    *`video-store.sh -run`
- manually executing the following commands in a rental-store-monolith directory:
    *`mvn clean install`
    *`mvn spring-boot:run`

# E2E tests
##There is a separate module called e2e-test. Before you run it, make sure the application is up and running on port 8080.
##(I'm aware that it could be more parametrized.)

##There are two options to run e2e test:
- using video-store.sh script
    *`video-store.sh -e2e`
- manually executing the following commands in e2e directory:
    *`mvn clean test`
- e2e test covers a happy scenario that would bring us money. Alternative paths are covered mostly by unit and integration tests

# Conscious decision:
- Skipped API authentication/authorization. You have to provide `Api-key` which in fact is used as userId. 
  Security is a huge topic and implementing it in production mode could take too much time. 
- Only the current number of customer points is presented.
- Lack of Customer in domain model. I focused on doing the rental engine nicely. I strongly believe that my application 
  is open for extensions, so it should not take much time to develop new features in the future.
- Skipped infrastructure part. The application uses h2 in memory db and that's the only external dependency. 
- Support for multiple currencies and new movie types (e.g PREMIUM) as it sounds like a candidate for the first new feature in the future ;)
