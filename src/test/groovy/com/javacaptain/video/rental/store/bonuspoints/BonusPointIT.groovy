package com.javacaptain.video.rental.store.bonuspoints

import com.javacaptain.video.rental.store.bonuspoints.api.BonusPoint
import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener
import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointsFacade
import com.javacaptain.video.rental.store.common.IntegrationTest
import com.javacaptain.video.rental.store.common.Money
import com.javacaptain.video.rental.store.common.MovieId
import com.javacaptain.video.rental.store.common.MovieType
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent
import com.javacaptain.video.rental.store.rental.api.ClientId
import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent
import com.javacaptain.video.rental.store.rental.api.RentalDate
import com.javacaptain.video.rental.store.rental.api.RentalId
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

class BonusPointIT extends IntegrationTest {

    @Autowired
    BonusPointEventListener bonusPointEventListener

    @Autowired
    BonusPointsFacade bonusPointsFacade

    def "Should add bonus points"() {
        given: "Movies has been added"
        def movieIds = setupMovies()

        and: "Client id"
        def clientId = new ClientId(UUID.randomUUID().toString())

        and: "Expected bonus points"
        def expectedPoints = new BonusPoint(4)

        and: "Rental created event"
        def event = new RentalCreatedEvent(
                new RentalId("1"),
                new RentalDate(LocalDate.now()),
                new Money(100.00, "USD"),
                clientId,
                movieIds
        )

        when: "Event has been handled"
        bonusPointEventListener.handle(event)

        then: "Client bonus points are correct"
        def points = bonusPointsFacade.getPoints(clientId)
        expectedPoints == points
    }

    private List<MovieId> setupMovies() {
        def movieIds = List.of(
                new MovieId("1"),
                new MovieId("2"),
                new MovieId("3")
        )
        def event1 = new MovieCreatedEvent(
                movieIds[0],
                MovieType.NEW_RELEASE,
                "Test title",
                "Test desc"
        )
        def event2 = new MovieCreatedEvent(
                movieIds[1],
                MovieType.REGULAR,
                "Test title",
                "Test desc"
        )

        def event3 = new MovieCreatedEvent(
                movieIds[2],
                MovieType.OLD,
                "Test title",
                "Test desc"
        )
        bonusPointEventListener.handle(event1)
        bonusPointEventListener.handle(event2)
        bonusPointEventListener.handle(event3)
        return movieIds
    }
}