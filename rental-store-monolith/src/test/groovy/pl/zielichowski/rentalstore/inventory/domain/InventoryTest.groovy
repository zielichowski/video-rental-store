package pl.zielichowski.rentalstore.inventory.domain

import org.axonframework.test.aggregate.AggregateTestFixture
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import pl.zielichowski.rentalstore.common.api.inventory.CreateInventoryCommand
import pl.zielichowski.rentalstore.common.api.inventory.InventoryCreatedEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent
import pl.zielichowski.rentalstore.common.api.inventory.MovieReturnedEvent
import pl.zielichowski.rentalstore.common.api.inventory.ReturnMovieCommand
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class InventoryTest extends Specification {
    @Shared
    def fixture = new AggregateTestFixture<>(Inventory)
    @Shared
    def inventoryId = UUID.randomUUID().toString()
    @Shared
    def firstMovieId = new MovieId()
    @Shared
    def secondMovieId = new MovieId()
    @Shared
    Map<MovieId, MovieInfo> movies = new HashMap<>()
    @Shared
    def inventoryOrderId = UUID.randomUUID().toString()

    def setup() {
        movies.put(firstMovieId, new MovieInfo("NewMovie", MovieTypeName.NEW))
        movies.put(secondMovieId, new MovieInfo("NewMovie2", MovieTypeName.NEW))

    }


    def "should create Inventory"() {
        given:
        def command = new CreateInventoryCommand(inventoryId, movies)
        def event = new InventoryCreatedEvent(command.getInventoryId(), command.getMovies())

        expect:
        fixture.given()
                .when(command)
                .expectEvents(event)
    }

    def "should validate inventory order with success"() {
        given:
        def inventoryCreatedEvent = new InventoryCreatedEvent(inventoryId, movies)

        def command = new ValidateInventoryOrderCommand(inventoryId, inventoryOrderId, movies.keySet() as List<MovieId>)
        def event = new InventoryOrderValidatedWithSuccessEvent(inventoryId, inventoryOrderId, movies.keySet() as List<MovieId>)

        expect:
        fixture.given(inventoryCreatedEvent)
                .when(command)
                .expectEvents(event)

    }

    @Unroll
    def "should validate inventory order with error when movie is already rented"(List<MovieId> moviesToRent) {
        given:
        def inventoryCreatedEvent = new InventoryCreatedEvent(inventoryId, movies)

        and: "validated movies with success"
        def validatedWithSuccessEvent = new InventoryOrderValidatedWithSuccessEvent(inventoryId, inventoryOrderId, movies.keySet() as List<MovieId>)

        def command = new ValidateInventoryOrderCommand(inventoryId, inventoryOrderId, moviesToRent)
        def event = new InventoryOrderValidatedWithErrorEvent(inventoryId, inventoryOrderId)

        expect:
        fixture.given(inventoryCreatedEvent, validatedWithSuccessEvent)
                .when(command)
                .expectEvents(event)

        where:
        moviesToRent                  | _
        [firstMovieId]                | _
        [firstMovieId, secondMovieId] | _
    }

    def "should return movie with success"() {
        given:
        def inventoryCreatedEvent = new InventoryCreatedEvent(inventoryId, movies)

        and: "validated movies with success"
        def validatedWithSuccessEvent = new InventoryOrderValidatedWithSuccessEvent(inventoryId, inventoryOrderId, movies.keySet() as List<MovieId>)

        def command = new ReturnMovieCommand(inventoryId, firstMovieId)
        def event = new MovieReturnedEvent(inventoryId, firstMovieId)

        expect:
        fixture.given(inventoryCreatedEvent, validatedWithSuccessEvent)
                .when(command)
                .expectEvents(event)
    }

    def "should not return movie if not exists"() {
        given:
        def inventoryCreatedEvent = new InventoryCreatedEvent(inventoryId, movies)

        and: "validated movies with success"
        def validatedWithSuccessEvent = new InventoryOrderValidatedWithSuccessEvent(inventoryId, inventoryOrderId, movies.keySet() as List<MovieId>)

        def randomMovieId = new MovieId()
        def command = new ReturnMovieCommand(inventoryId, randomMovieId)

        expect:
        fixture.given(inventoryCreatedEvent, validatedWithSuccessEvent)
                .when(command)
                .expectException(RuntimeException)
    }

    def "should not return movie if not rented"() {
        given:
        def inventoryCreatedEvent = new InventoryCreatedEvent(inventoryId, movies)

        def command = new ReturnMovieCommand(inventoryId, firstMovieId)

        expect:
        fixture.given(inventoryCreatedEvent)
                .when(command)
                .expectException(IllegalArgumentException)
    }
}
