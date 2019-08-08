package pl.zielichowski.rentalstore.common.api.domain

import spock.lang.Specification
import spock.lang.Unroll

class ProgressivePricingAlgorithmTest extends Specification {
    @Unroll
    def 'should calculate price'(Money basePrice, Integer daysOfRental, Money expectedPrice, Integer threshold) {
        given:
        def progressivePricingAlgorithm = new ProgressivePricingAlgorithm(threshold)
        when:
        def price = progressivePricingAlgorithm.calculatePrice(daysOfRental, basePrice)
        then:
        price == expectedPrice

        where:
        basePrice                   | daysOfRental | expectedPrice            | threshold
        new Money(40.00, "SEK")     | 1            | new Money(40.00, "SEK")  | 1
        new Money(1, "SEK")         | 2            | new Money(1.00, "SEK")   | 2
        new Money(10.00, "PLN")     | 5            | new Money(30.00, "PLN")  | 3
        new Money(10, "SEK")        | 10           | new Money(60.00, "SEK")  | 5
        new Money(133.33999, "SEK") | 3            | new Money(533.36, "SEK") | 0
    }
}
