package pl.zielichowski.rentalstore.rental.domain

import org.axonframework.test.saga.SagaTestFixture
import pl.zielichowski.rentalstore.common.api.domain.Money
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import pl.zielichowski.rentalstore.common.api.inventory.CreateInventoryOrderCommand
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent
import pl.zielichowski.rentalstore.common.api.inventory.ReturnMovieCommand
import pl.zielichowski.rentalstore.common.api.rental.AcceptRentalCommand
import pl.zielichowski.rentalstore.common.api.rental.CalculatePossibleSurchargesCommand
import pl.zielichowski.rentalstore.common.api.rental.RejectRentalCommand
import pl.zielichowski.rentalstore.common.api.rental.RentalAcceptedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentalFinishedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentalItem
import pl.zielichowski.rentalstore.common.api.rental.RentalItemReturnedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentalSubmittedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentedMovie
import pl.zielichowski.rentalstore.common.api.rental.RentedMovies
import pl.zielichowski.rentalstore.common.api.rental.SurchargeCalculatedEvent
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

class RentalSagaTest extends Specification {
    def fixture = new SagaTestFixture(RentalSaga)
    @Shared
    def rentalId = UUID.randomUUID().toString()
    @Shared
    def inventoryId = UUID.randomUUID().toString()
    @Shared
    def firstRentalItem = new RentalItem(new MovieId("1"), MovieTypeName.NEW, 2)
    @Shared
    def secondRentalItem = new RentalItem(new MovieId("2"), MovieTypeName.REGULAR, 10)
    @Shared
    def rentalItems = [firstRentalItem, secondRentalItem]
    @Shared
    def rentalDate = LocalDate.of(2019, 4, 22)
    @Shared
    def userId = "userId1"
    @Shared
    def inventoryOrderId = 'inventoryOrder_' + rentalId

    def "should start saga when rental is submitted"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def inventoryOrderCommand = new CreateInventoryOrderCommand(inventoryOrderId, inventoryId, [firstRentalItem.movieId, secondRentalItem.movieId])

        fixture.givenNoPriorActivity()
                .whenAggregate(rentalId)
                .publishes(rentalSubmittedEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(inventoryOrderCommand)
    }

    def "should try to complete rental when inventory order is validated with success"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def validatedWithSuccessEvent = new InventoryOrderValidatedWithSuccessEvent(inventoryId, inventoryOrderId, [firstRentalItem.movieId, secondRentalItem.movieId])

        def completeRentalCommand = new AcceptRentalCommand(rentalId)

        fixture.givenAggregate(rentalId)
                .published(rentalSubmittedEvent)
                .whenPublishingA(validatedWithSuccessEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(completeRentalCommand)
    }

    def "should end saga rental when inventory order is validated with error"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def validatedWithErrorEvent = new InventoryOrderValidatedWithErrorEvent(inventoryId, inventoryOrderId)
        def rejectRentalCommand = new RejectRentalCommand(rentalId)

        fixture.givenAggregate(rentalId)
                .published(rentalSubmittedEvent)
                .whenPublishingA(validatedWithErrorEvent)
                .expectActiveSagas(0)
                .expectDispatchedCommands(rejectRentalCommand)
    }

    def "should end saga on rental finished event"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rentalFinishedEvent = new RentalFinishedEvent(rentalId, new Money(10.00))

        fixture.givenAggregate(rentalId)
                .published(rentalSubmittedEvent)
                .whenPublishingA(rentalFinishedEvent)
                .expectActiveSagas(0)
                .expectNoDispatchedCommands()
    }

    def "should not publish any commands on rental completed event"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rentalAcceptedEvent = new RentalAcceptedEvent(rentalId, userId, rentalDate, new RentedMovies(new ArrayList<RentedMovie>(), new Money(10, "PLN")))


        fixture.givenAggregate(rentalId)
                .published(rentalSubmittedEvent)
                .whenPublishingA(rentalAcceptedEvent)
                .expectActiveSagas(1)
                .expectNoDispatchedCommands()
    }

    def "should return rental item"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def itemReturnedEvent = new RentalItemReturnedEvent(rentalId, firstRentalItem.movieId, LocalDate.of(2019, 5, 25))
        def returnMovieCommand = new ReturnMovieCommand(inventoryId, firstRentalItem.movieId)
        def calculatePossibleSurchargesCommand = new CalculatePossibleSurchargesCommand(rentalId, firstRentalItem.movieId, LocalDate.of(2019, 5, 25))

        expect:
        fixture.givenAggregate(rentalId)
                .published(rentalSubmittedEvent)
                .whenPublishingA(itemReturnedEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(
                        returnMovieCommand,
                        calculatePossibleSurchargesCommand)
    }

    def "should validate returned rental item"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def surchargeCalculatedEvent = new SurchargeCalculatedEvent(rentalId, firstRentalItem.movieId, Money.ZERO)
        def validateReturnedRentalItemsCommand = new ValidateReturnedRentalItemsCommand(rentalId)

        expect:
        fixture.givenAggregate(rentalId)
                .published(rentalSubmittedEvent)
                .whenPublishingA(surchargeCalculatedEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(
                        validateReturnedRentalItemsCommand)
    }
}
