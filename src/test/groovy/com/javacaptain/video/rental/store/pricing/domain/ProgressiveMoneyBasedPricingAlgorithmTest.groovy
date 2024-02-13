package com.javacaptain.video.rental.store.pricing.domain

import com.javacaptain.video.rental.store.common.Money
import com.javacaptain.video.rental.store.pricing.domain.PricingAlgorithm
import spock.lang.Specification
import spock.lang.Unroll

class ProgressiveMoneyBasedPricingAlgorithmTest extends Specification {
    @Unroll
    def 'should calculate price'(Money basePrice, Integer daysOfRental, Money expectedPrice, Integer threshold) {
        given:
        def progressivePricingAlgorithm = new PricingAlgorithm.ProgressivePricingAlgorithm(threshold)
        when:
        def price = progressivePricingAlgorithm.calculatePrice(daysOfRental, basePrice)
        then:
        price == expectedPrice

        where:
        basePrice                   | daysOfRental | expectedPrice            | threshold
        new Money(40.00, "USD")     | 1            | new Money(40.00, "USD")  | 1
        new Money(1, "USD")         | 2            | new Money(1.00, "USD")   | 2
        new Money(10.00, "USD")     | 5            | new Money(30.00, "USD")  | 3
        new Money(10, "USD")        | 10           | new Money(60.00, "USD")  | 5
        new Money(133.33999, "USD") | 3            | new Money(533.36, "USD") | 0
    }
}
