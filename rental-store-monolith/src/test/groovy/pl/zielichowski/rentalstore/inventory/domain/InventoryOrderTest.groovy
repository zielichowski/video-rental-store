package pl.zielichowski.rentalstore.inventory.domain

import org.axonframework.test.aggregate.AggregateTestFixture
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import pl.zielichowski.rentalstore.common.api.inventory.CreateInventoryOrderCommand
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderCreatedEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent
import spock.lang.Shared
import spock.lang.Specification

class InventoryOrderTest extends Specification {
    @Shared
    def fixture = new AggregateTestFixture<>(InventoryOrder)

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

    def "should create inventory order"() {
        given:
        def command = new CreateInventoryOrderCommand(inventoryOrderId, inventoryId, movies)
        def event = new InventoryOrderCreatedEvent(command.getInventoryOrderId(), command.getInventoryId(), command.getMovies())

        expect:
        fixture
                .when(command)
                .expectEvents(event)
    }

    def "should validate inventory order with success"() {
        given:
        def createdEvent = new InventoryOrderCreatedEvent(inventoryOrderId, inventoryId, movies)
        def withSuccessEvent = new InventoryOrderValidatedWithSuccessEvent(inventoryId, inventoryOrderId, movies)

        expect:
        fixture
                .given(createdEvent)
                .when(withSuccessEvent)
                .expectNoEvents()
    }

    def "should validate inventory order with error"() {
        given:
        def createdEvent = new InventoryOrderCreatedEvent(inventoryOrderId, inventoryId, movies)
        def withErrorEvent = new InventoryOrderValidatedWithErrorEvent(inventoryId, inventoryOrderId)

        expect:
        fixture
                .given(createdEvent)
                .when(withErrorEvent)
                .expectNoEvents()
    }
}
