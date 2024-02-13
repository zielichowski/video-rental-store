package com.javacaptain.video.rental.store.pricing.domain

import com.javacaptain.video.rental.store.common.*
import com.javacaptain.video.rental.store.pricing.api.PriceRequest
import com.javacaptain.video.rental.store.pricing.api.SurchargeRequest
import com.javacaptain.video.rental.store.pricing.infrastructure.InMemoryPricingMovieService
import spock.lang.Specification

class PricingFacadeTest extends Specification {
    def pricingMovieService = new InMemoryPricingMovieService(new MockPricingMovieRepository())
    def surchargeCalculator = new DefaultSurchargeCalculator(pricingMovieService)
    def pricingFacade = new PricingFacade(surchargeCalculator, pricingMovieService)

    def setup() {
        pricingMovieService.saveMovie(new PricingMovie(new MovieId("1"), MovieType.NEW_RELEASE))
        pricingMovieService.saveMovie(new PricingMovie(new MovieId("2"), MovieType.REGULAR))
        pricingMovieService.saveMovie(new PricingMovie(new MovieId("3"), MovieType.OLD))
    }

    def "Should calculate a price of a single item"() {
        given: "Pricing facade"
        def pricingRequest = new PriceRequest(new MovieId("1"), new RentalPeriod(4))

        when:
        def price = pricingFacade.calculatePrice(pricingRequest)

        then:
        price == new Money(160.00, "USD")
    }

    def "Should calculate a total price for multiple items"() {
        given:
        def pricingRequests = List.of(
                new PriceRequest(new MovieId("1"), new RentalPeriod(4)),
                new PriceRequest(new MovieId("2"), new RentalPeriod(5)),
                new PriceRequest(new MovieId("3"), new RentalPeriod(10)))

        and:
        def expectedPrice = new Money(430.00, "USD")
        when:
        def price = pricingFacade.calculatePrice(pricingRequests)

        then:
        price == expectedPrice
    }

    def "Should calculate surcharge"(SurchargeRequest surchargeRequest, Money expectedSurcharge) {
        when:
        def surcharge = pricingFacade.calculateSurcharge(surchargeRequest)

        then:
        surcharge == expectedSurcharge

        where:
        surchargeRequest                                               | expectedSurcharge
        new SurchargeRequest(new MovieId("1"), new ExtraRentalDays(4)) | new Money(160.00, "USD")
        new SurchargeRequest(new MovieId("2"), new ExtraRentalDays(5)) | new Money(150.00, "USD")
        new SurchargeRequest(new MovieId("3"), new ExtraRentalDays(4)) | new Money(120.00, "USD")
    }

    def "Should calculate total surcharge"() {
        given:
        def surchargeRequests = List.of(
                new SurchargeRequest(new MovieId("1"), new ExtraRentalDays(4)),
                new SurchargeRequest(new MovieId("2"), new ExtraRentalDays(5)),
                new SurchargeRequest(new MovieId("3"), new ExtraRentalDays(4))
        )
        and:
        def expectedSurcharge = new Money(430, "USD")
        when:
        def surcharge = pricingFacade.calculateSurcharge(surchargeRequests)

        then:
        surcharge == expectedSurcharge
    }

}