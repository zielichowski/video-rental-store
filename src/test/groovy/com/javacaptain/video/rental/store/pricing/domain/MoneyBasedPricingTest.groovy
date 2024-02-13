package com.javacaptain.video.rental.store.pricing.domain

import com.javacaptain.video.rental.store.common.Money
import spock.lang.Specification

class MoneyBasedPricingTest extends Specification {

    def "Should calculate price"(MoneyBasedPricing pricing, Integer daysOfRental, Money expectedPrice) {
        when:
        def price = pricing.price(daysOfRental)

        then:
        expectedPrice == price

        where:
        pricing                                                                                          | daysOfRental | expectedPrice
        new MoneyBasedPricing(new Money(40, "USD"), new PricingAlgorithm.FixedPricingAlgorithm())        | 1            | new Money(40.00, "USD")
        new MoneyBasedPricing(new Money(20, "USD"), new PricingAlgorithm.ProgressivePricingAlgorithm(3)) | 2            | new Money(20.00, "USD")
        new MoneyBasedPricing(new Money(40, "USD"), new PricingAlgorithm.ProgressivePricingAlgorithm(5)) | 10           | new Money(240.00, "USD")
    }

}