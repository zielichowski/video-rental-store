package com.javacaptain.video.rental.store.pricing.domain

import com.javacaptain.video.rental.store.common.Money
import com.javacaptain.video.rental.store.common.MovieType
import com.javacaptain.video.rental.store.pricing.domain.MoneyBasedPricing
import com.javacaptain.video.rental.store.pricing.domain.PricingAlgorithm
import com.javacaptain.video.rental.store.pricing.domain.PricingFactory
import spock.lang.Specification


class MoneyBasedPricingFactoryTest extends Specification {
    def "should create a proper pricing"(MovieType movieType, MoneyBasedPricing pricing) {
        when: "Creating pricing from movie type"
        def expectedPricing = PricingFactory.from(movieType)

        then: "Correct pricing has been created"
        expectedPricing == pricing

        where:
        movieType             | pricing
        MovieType.NEW_RELEASE | new MoneyBasedPricing(
                                    new Money(new BigDecimal(40), "USD"),
                                    new PricingAlgorithm.FixedPricingAlgorithm())
        MovieType.REGULAR     | new MoneyBasedPricing(
                                    new Money(new BigDecimal(30), "USD"),
                                    new PricingAlgorithm.ProgressivePricingAlgorithm(3))
        MovieType.OLD         | new MoneyBasedPricing(
                                     new Money(new BigDecimal(30), "USD"),
                                     new PricingAlgorithm.ProgressivePricingAlgorithm(5))


    }

}