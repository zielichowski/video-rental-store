package pl.zielichowski.rentalstore.rental.domain

import org.axonframework.test.aggregate.AggregateTestFixture
import pl.zielichowski.rentalstore.common.api.domain.Money
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import pl.zielichowski.rentalstore.common.api.rental.*
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

class RentalTest extends Specification {
    @Shared
    def fixture = new AggregateTestFixture(Rental)
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

    def "should create rental"() {
        given:
        def submitRentalCommand = new SubmitRentalCommand(rentalId, inventoryId, userId, rentalItems, rentalDate)
        def event = new RentalSubmittedEvent(
                submitRentalCommand.rentalId,
                submitRentalCommand.inventoryId,
                submitRentalCommand.userId,
                submitRentalCommand.rentalItems,
                submitRentalCommand.date)
        expect:
        fixture
                .when(submitRentalCommand)
                .expectEvents(event)
    }

    def "should reject rental"() {
        given:
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rejectRentalCommand = new RejectRentalCommand(rentalId)
        def rentalRejectedEvent = new RentalRejectedEvent(rentalId, userId)
        expect:
        fixture.given(rentalSubmittedEvent)
                .when(rejectRentalCommand)
                .expectEvents(rentalRejectedEvent)
    }

    def "should accept rental"() {
        given:
        def acceptRentalCommand = new AcceptRentalCommand(rentalId)
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)

        def rentedMovie1 = new RentedMovie(firstRentalItem.movieId, firstRentalItem.daysOfRental, new Money(80, "SEK"))
        def rentedMovie2 = new RentedMovie(secondRentalItem.movieId, secondRentalItem.daysOfRental, new Money(240, "SEK"))
        def rentedMovies = new RentedMovies([rentedMovie1, rentedMovie2], new Money(320, "SEK"))
        def rentalAcceptedEvent = new RentalAcceptedEvent(rentalId, userId, rentalDate, rentedMovies)
        def bonusPointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)

        expect:
        fixture.given(rentalSubmittedEvent)
                .when(acceptRentalCommand)
                .expectEvents(rentalAcceptedEvent, bonusPointsCalculatedEvent)
    }

    def "should return rental item"() {
        given:
        def returnRentalItemCommand = new ReturnRentalItemCommand(rentalId, firstRentalItem.movieId, LocalDate.of(2019, 5, 25))
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rentalConfirmedEvent = new RentalConfirmedEvent(rentalId)
        def bonusPointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)

        def rentedMovie1 = new RentedMovie(firstRentalItem.movieId, firstRentalItem.daysOfRental, new Money(80, "SEK"))
        def rentedMovie2 = new RentedMovie(secondRentalItem.movieId, secondRentalItem.daysOfRental, new Money(240, "SEK"))
        def rentedMovies = new RentedMovies([rentedMovie2, rentedMovie1], new Money(320, "SEK"))
        def rentalCompletedEvent = new RentalAcceptedEvent(rentalId, userId, rentalDate, rentedMovies)
        def rentalItemReturnedEvent = new RentalItemReturnedEvent(rentalId, firstRentalItem.movieId, LocalDate.of(2019, 5, 25))

        expect:
        fixture.given(rentalSubmittedEvent, rentalConfirmedEvent, bonusPointsCalculatedEvent, rentalCompletedEvent)
                .when(returnRentalItemCommand)
                .expectEvents(rentalItemReturnedEvent)
    }

    def "should not return rental item if not exists"() {
        given:
        def returnRentalItemCommand = new ReturnRentalItemCommand(rentalId, new MovieId("Fake movie Id"), LocalDate.of(2019, 5, 25))
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rentalConfirmedEvent = new RentalConfirmedEvent(rentalId)
        def priceCalculatedEvent = new PriceCalculatedEvent(rentalId, new Money(320, "SEK"))
        def bonusPointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)

        def rentedMovie1 = new RentedMovie(firstRentalItem.movieId, firstRentalItem.daysOfRental, new Money(80, "SEK"))
        def rentedMovie2 = new RentedMovie(secondRentalItem.movieId, secondRentalItem.daysOfRental, new Money(240, "SEK"))
        def rentedMovies = new RentedMovies([rentedMovie2, rentedMovie1], new Money(320, "SEK"))
        def rentalCompletedEvent = new RentalAcceptedEvent(rentalId, userId, rentalDate, rentedMovies)

        expect:
        fixture.given(rentalSubmittedEvent, rentalConfirmedEvent, priceCalculatedEvent, bonusPointsCalculatedEvent, rentalCompletedEvent)
                .when(returnRentalItemCommand)
                .expectNoEvents()
    }

    def "should calculate surcharge"() {
        given:
        def calculatePossibleSurchargesCommand = new CalculatePossibleSurchargesCommand(rentalId, firstRentalItem.movieId, LocalDate.of(2019, 4, 30))
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rentalConfirmedEvent = new RentalConfirmedEvent(rentalId)
        def priceCalculatedEvent = new PriceCalculatedEvent(rentalId, new Money(320, "SEK"))
        def bonusPointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)

        def rentedMovie1 = new RentedMovie(firstRentalItem.movieId, firstRentalItem.daysOfRental, new Money(80, "SEK"))
        def rentedMovie2 = new RentedMovie(secondRentalItem.movieId, secondRentalItem.daysOfRental, new Money(240, "SEK"))
        def rentedMovies = new RentedMovies([rentedMovie2, rentedMovie1], new Money(320, "SEK"))
        def rentalCompletedEvent = new RentalAcceptedEvent(rentalId, userId, rentalDate, rentedMovies)
        def rentalItemReturnedEvent = new RentalItemReturnedEvent(rentalId, firstRentalItem.movieId, LocalDate.of(2019, 5, 25))

        def surchargeCalculatedEvent = new SurchargeCalculatedEvent(rentalId, rentedMovie1.movieId, new Money(240, "SEK"))
        expect:
        fixture.given(rentalSubmittedEvent, rentalConfirmedEvent, priceCalculatedEvent,
                bonusPointsCalculatedEvent, rentalCompletedEvent, rentalItemReturnedEvent)
                .when(calculatePossibleSurchargesCommand)
                .expectEvents(surchargeCalculatedEvent)
    }

    def "should finished rental with surcharge when all items are returned late"() {
        given:
        def validateReturnedRentalItemsCommand = new ValidateReturnedRentalItemsCommand(rentalId)
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rentalConfirmedEvent = new RentalConfirmedEvent(rentalId)
        def priceCalculatedEvent = new PriceCalculatedEvent(rentalId, new Money(320, "SEK"))
        def bonusPointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)

        def rentedMovie1 = new RentedMovie(firstRentalItem.movieId, firstRentalItem.daysOfRental, new Money(80, "SEK"))
        def rentedMovie2 = new RentedMovie(secondRentalItem.movieId, secondRentalItem.daysOfRental, new Money(240, "SEK"))
        def rentedMovies = new RentedMovies([rentedMovie2, rentedMovie1], new Money(320, "SEK"))
        def rentalCompletedEvent = new RentalAcceptedEvent(rentalId, userId, rentalDate, rentedMovies)

        def rentalItemReturnedEvent1 = new RentalItemReturnedEvent(rentalId, rentedMovie1.movieId, LocalDate.of(2019, 5, 5))
        def rentalItemReturnedEvent2 = new RentalItemReturnedEvent(rentalId, rentedMovie2.movieId, LocalDate.of(2019, 5, 5))

        def surchargeCalculatedEvent1 = new SurchargeCalculatedEvent(rentalId, rentedMovie1.movieId, new Money(240, "SEK"))
        def surchargeCalculatedEvent2 = new SurchargeCalculatedEvent(rentalId, rentedMovie2.movieId, new Money(100, "SEK"))

        def rentalFinishedEvent = new RentalFinishedEvent(rentalId, new Money(340, "SEK"))
        expect:
        fixture.given(rentalSubmittedEvent, rentalConfirmedEvent, priceCalculatedEvent,
                bonusPointsCalculatedEvent, rentalCompletedEvent, rentalItemReturnedEvent1,
                surchargeCalculatedEvent1, rentalItemReturnedEvent2, surchargeCalculatedEvent2)
                .when(validateReturnedRentalItemsCommand)
                .expectEvents(rentalFinishedEvent)
    }

    def "should finished rental without surcharge when all items are returned"() {
        given:
        def validateReturnedRentalItemsCommand = new ValidateReturnedRentalItemsCommand(rentalId)
        def rentalSubmittedEvent = new RentalSubmittedEvent(
                rentalId,
                inventoryId,
                userId,
                rentalItems,
                rentalDate)
        def rentalConfirmedEvent = new RentalConfirmedEvent(rentalId)
        def priceCalculatedEvent = new PriceCalculatedEvent(rentalId, new Money(320, "SEK"))
        def bonusPointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)

        def rentedMovie1 = new RentedMovie(firstRentalItem.movieId, firstRentalItem.daysOfRental, new Money(80, "SEK"))
        def rentedMovie2 = new RentedMovie(secondRentalItem.movieId, secondRentalItem.daysOfRental, new Money(240, "SEK"))
        def rentedMovies = new RentedMovies([rentedMovie2, rentedMovie1], new Money(320, "SEK"))
        def rentalCompletedEvent = new RentalAcceptedEvent(rentalId, userId, rentalDate, rentedMovies)

        def rentalItemReturnedEvent1 = new RentalItemReturnedEvent(rentalId, rentedMovie1.movieId, rentalDate)
        def rentalItemReturnedEvent2 = new RentalItemReturnedEvent(rentalId, rentedMovie2.movieId, rentalDate)

        def surchargeCalculatedEvent1 = new SurchargeCalculatedEvent(rentalId, rentedMovie1.movieId, new Money(0, "SEK"))
        def surchargeCalculatedEvent2 = new SurchargeCalculatedEvent(rentalId, rentedMovie2.movieId, new Money(0, "SEK"))


        def rentalFinishedEvent = new RentalFinishedEvent(rentalId, new Money(0, "SEK"))
        expect:
        fixture.given(rentalSubmittedEvent, rentalConfirmedEvent, priceCalculatedEvent,
                bonusPointsCalculatedEvent, rentalCompletedEvent, rentalItemReturnedEvent1,
                surchargeCalculatedEvent1, rentalItemReturnedEvent2, surchargeCalculatedEvent2)
                .when(validateReturnedRentalItemsCommand)
                .expectEvents(rentalFinishedEvent)
    }
}
