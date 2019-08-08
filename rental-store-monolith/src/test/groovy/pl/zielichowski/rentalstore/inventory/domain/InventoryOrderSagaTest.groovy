package pl.zielichowski.rentalstore.inventory.domain

import org.axonframework.test.saga.SagaTestFixture
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderCreatedEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent
import spock.lang.Shared
import spock.lang.Specification

class InventoryOrderSagaTest extends Specification {

    def fixture = new SagaTestFixture(InventoryOrderSaga)

    @Shared
    def inventoryId = UUID.randomUUID().toString()
    @Shared
    def firstMovieId = new MovieId()
    @Shared
    def secondMovieId = new MovieId()
    @Shared
    def movies = new ArrayList()
    @Shared
    def inventoryOrderId = UUID.randomUUID().toString()

    def setup() {
        movies.add(new Movie(firstMovieId, new MovieInfo("NewMovie", MovieTypeName.NEW), MovieStatus.AVAILABLE))
        movies.add(new Movie(secondMovieId, new MovieInfo("NewMovie2", MovieTypeName.NEW), MovieStatus.AVAILABLE))

    }

    def "should start saga when inventory order is created"() {
        given:
        def inventoryOrderCreatedEvent = new InventoryOrderCreatedEvent(inventoryOrderId, inventoryId, movies)
        def validateInventoryOrderCommand = new ValidateInventoryOrderCommand(inventoryId, inventoryOrderId, movies)
        expect:
        fixture.givenNoPriorActivity()
                .whenAggregate(inventoryOrderId)
                .publishes(inventoryOrderCreatedEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(validateInventoryOrderCommand)
    }

    def "should end saga when inventory order is validated with success"() {
        given:
        def validatedWithSuccessEvent = new InventoryOrderValidatedWithSuccessEvent(inventoryOrderId, inventoryId, movies)

        expect:
        fixture.givenNoPriorActivity()
                .whenAggregate(inventoryId)
                .publishes(validatedWithSuccessEvent)
                .expectNoDispatchedCommands()
                .expectActiveSagas(0)
    }

    def "should end saga when inventory order is validated with error"() {
        given:
        def validatedWithErrorEvent = new InventoryOrderValidatedWithErrorEvent(inventoryOrderId, inventoryId)

        expect:
        fixture.givenNoPriorActivity()
                .whenAggregate(inventoryId)
                .publishes(validatedWithErrorEvent)
                .expectNoDispatchedCommands()
                .expectActiveSagas(0)
    }

}
