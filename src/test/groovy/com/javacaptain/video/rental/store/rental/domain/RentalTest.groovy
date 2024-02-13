package com.javacaptain.video.rental.store.rental.domain

import com.javacaptain.video.rental.store.common.ExtraRentalDays
import com.javacaptain.video.rental.store.common.Money
import com.javacaptain.video.rental.store.common.MovieId
import com.javacaptain.video.rental.store.common.RentalPeriod
import com.javacaptain.video.rental.store.rental.api.*
import spock.lang.Specification

import java.time.LocalDate

class RentalTest extends Specification {

    def "Should calculate late returns"() {
        given:
        def rentalId = new RentalId(UUID.randomUUID().toString())
        and:
        def rental = rental(rentalId)
        and:
        def expectedLateReturns = expectedLateReturns()

        when:
        def lateReturns = rental.returnItems(new ReturnDate(LocalDate.parse("2024-01-15")))

        then:
        lateReturns == expectedLateReturns
    }

    private static List<LateReturn> expectedLateReturns() {
        List.of(new LateReturn(new MovieId("1"), new ExtraRentalDays(9)),
                new LateReturn(new MovieId("2"), new ExtraRentalDays(11)))
    }

    private static Rental rental(RentalId rentalId) {
        new Rental(rentalId,
                new RentalDate(LocalDate.parse("2024-01-01")),
                new Money(20, "USD"),
                rentalItems(),
                new ClientId("1"))
    }

    private static List<RentalItem> rentalItems() {
        List.of(new RentalItem(new MovieId("1"), new RentalPeriod(5)),
                new RentalItem(new MovieId("2"), new RentalPeriod(3)),
                new RentalItem(new MovieId("3"), new RentalPeriod(14)))
    }

}