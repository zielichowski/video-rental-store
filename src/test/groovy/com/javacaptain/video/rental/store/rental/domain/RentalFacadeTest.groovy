package com.javacaptain.video.rental.store.rental.domain

import com.javacaptain.video.rental.store.common.Money
import com.javacaptain.video.rental.store.common.MovieId
import com.javacaptain.video.rental.store.common.RentalPeriod
import com.javacaptain.video.rental.store.rental.api.*
import com.javacaptain.video.rental.store.rental.config.MockPricingAdapter
import com.javacaptain.video.rental.store.rental.config.MockRentalEventPublisher
import com.javacaptain.video.rental.store.rental.config.MockRentalRepository
import com.javacaptain.video.rental.store.rental.config.MockReturnRepository
import com.javacaptain.video.rental.store.rental.web.CreateRentalRequest
import spock.lang.Shared
import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import static com.javacaptain.video.rental.store.rental.config.RentalFixtures.rentalDto

class RentalFacadeTest extends Specification {
    @Shared
    def rentalRepository = new MockRentalRepository()
    @Shared
    def returnRepository = new MockReturnRepository()
    @Shared
    def clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    @Shared
    def rentalEventPublisher = new MockRentalEventPublisher()
    @Shared
    def rentalFacade = new RentalFacade(
            new MockPricingAdapter(),
            rentalRepository,
            returnRepository,
            rentalEventPublisher,
            clock,
    )

    def "Should create rental"() {
        given:
        def rentalItems = List.of(
                rentalDto("1", 5),
                rentalDto("2", 6))
        def createRequest = new CreateRentalRequest(rentalItems,"1")

        when:
        def actualRental = rentalFacade.rent(
                createRequest,
        )

        and:
        def expectedRental = rental(actualRental.rentalId())

        then:
        actualRental == expectedRental
    }

    def "Should return rental items and calculate surcharge"() {
        given:
        def rentalId = new RentalId(UUID.randomUUID().toString())
        and:
        def rental = rental(rentalId)
        and:
        rentalRepository.save(rental)

        when:
        def rentalReturn = rentalFacade.returnRental(rentalId)

        and:
        def expectedRentalReturn = new Return(
                rentalReturn.returnId(),
                rentalId,
                new ReturnDate(LocalDate.now(clock)),
                new Money(0.00, "USD"))

        then:
        rentalReturn == expectedRentalReturn
    }

    private Rental rental(RentalId rentalId) {
        new Rental(
                rentalId,
                new RentalDate(LocalDate.now(clock)),
                new Money(20, "USD"),
                [new RentalItem(new MovieId("1"), new RentalPeriod(5)),
                 new RentalItem(new MovieId("2"), new RentalPeriod(6))],
                new ClientId("1")
        )
    }

}