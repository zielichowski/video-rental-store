package com.javacaptain.video.rental.store.pricing.domain

import com.javacaptain.video.rental.store.common.Money
import com.javacaptain.video.rental.store.pricing.domain.PricingAlgorithm
import spock.lang.Specification
import spock.lang.Unroll

class FixedMoneyBasedPricingAlgorithmTest extends Specification {
    @Unroll
    def 'should calculate price'(Money basePrice, Integer daysOfRental, Money expectedPrice) {
        given:
        def fixedPricingAlgorithm = new PricingAlgorithm.FixedPricingAlgorithm()
        when:
        def price = fixedPricingAlgorithm.calculatePrice(daysOfRental, basePrice)
        then:
        price == expectedPrice

        where:
        basePrice                   | daysOfRental | expectedPrice
        new Money(40.00, "USD")     | 1            | new Money(40.00, "USD")
        new Money(1, "USD")         | 2            | new Money(2.00, "USD")
        new Money(10.00, "USD")     | 3            | new Money(30.00, "USD")
        new Money(0, "USD")         | 2            | new Money(0.00, "USD")
        new Money(133.33999, "USD") | 3            | new Money(400.02, "USD")
    }
}
