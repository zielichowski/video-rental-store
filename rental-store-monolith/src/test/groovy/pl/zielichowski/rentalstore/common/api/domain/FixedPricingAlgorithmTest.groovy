package pl.zielichowski.rentalstore.common.api.domain

import spock.lang.Specification
import spock.lang.Unroll

class FixedPricingAlgorithmTest extends Specification {
    @Unroll
    def 'should calculate price'(Money basePrice, Integer daysOfRental, Money expectedPrice) {
        given:
        def fixedPricingAlgorithm = new FixedPricingAlgorithm()
        when:
        def price = fixedPricingAlgorithm.calculatePrice(daysOfRental, basePrice)
        then:
        price == expectedPrice

        where:
        basePrice                   | daysOfRental | expectedPrice
        new Money(40.00, "SEK")     | 1            | new Money(40.00, "SEK")
        new Money(1, "SEK")         | 2            | new Money(2.00, "SEK")
        new Money(10.00, "PLN")     | 3            | new Money(30.00, "PLN")
        new Money(0, "SEK")         | 2            | new Money(0.00, "SEK")
        new Money(133.33999, "SEK") | 3            | new Money(400.02, "SEK")
    }
}
