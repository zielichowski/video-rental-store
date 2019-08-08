package pl.zielichowski.rentalstore.rental.domain

import pl.zielichowski.rentalstore.common.api.domain.*

class MovieType private constructor(private val name: String,
                                    private val bonusPoints: Int,
                                    private val pricing: PricingAlgorithm,
                                    private val basePrice: Money)
    : Priceable, Pointable, Surchargeable {
    override fun calculateBonusPoints(): Int {
        return bonusPoints
    }

    override fun calculateSurcharge(daysOfDelay: Int): Money {
        return basePrice.multiplyBy(daysOfDelay.toBigDecimal())
    }

    override fun calculatePrice(daysOfRental: Int): Money {
        return pricing.calculatePrice(daysOfRental, basePrice)
    }

    companion object {
        fun of(movieTypeName: MovieTypeName, pricing: PricingAlgorithm, basePrice: Money, bonusPoints: Int) =
                MovieType(movieTypeName.name, bonusPoints, pricing, basePrice)

        fun of(movieTypeName: MovieTypeName) = when (movieTypeName) {
            MovieTypeName.NEW -> NEW
            MovieTypeName.REGULAR -> REGULAR
            MovieTypeName.OLD -> OLD
        }

        @JvmField
        val NEW = of(MovieTypeName.NEW, FixedPricingAlgorithm(), Money(40.00, "SEK"), 2)
        @JvmField
        val REGULAR = of(MovieTypeName.REGULAR, ProgressivePricingAlgorithm(3), Money(30.00, "SEK"), 1)
        @JvmField
        val OLD = of(MovieTypeName.OLD, ProgressivePricingAlgorithm(5), Money(30.00, "SEK"), 1)
    }
}