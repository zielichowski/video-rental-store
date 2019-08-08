package pl.zielichowski.rentalstore.common.api.domain

import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable


interface Pointable {
    fun calculateBonusPoints(): Int
}

interface Surchargeable {
    fun calculateSurcharge(daysOfDelay: Int): Money
}

interface Priceable {
    fun calculatePrice(daysOfRental: Int): Money
}

interface PricingAlgorithm {
    fun calculatePrice(daysOfRental: Int, basePrice: Money): Money
}

class FixedPricingAlgorithm : PricingAlgorithm {
    override fun calculatePrice(daysOfRental: Int, basePrice: Money): Money {
        return basePrice.multiplyBy(daysOfRental.toDouble())
    }
}

class ProgressivePricingAlgorithm(private val threshold: Int) : PricingAlgorithm {
    override fun calculatePrice(daysOfRental: Int, basePrice: Money): Money {
        return if (daysOfRental > threshold) {
            val multiplier = daysOfRental - threshold
            val money = basePrice.multiplyBy(multiplier.toDouble())
            basePrice.add(money)
        } else
            basePrice
    }
}


enum class MovieTypeName {
    NEW, REGULAR, OLD
}

data class MovieId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MovieId
        if (identifier != other.identifier) return false
        return true
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

class MovieInfo {
    lateinit var title: String
    lateinit var type: MovieTypeName

    constructor()
    constructor(title: String, movieTypeName: MovieTypeName) {
        this.title = title
        this.type = movieTypeName
    }
}

@Embeddable
@Access(AccessType.FIELD)
class Money : Serializable {

    private lateinit var denomination: BigDecimal
    private lateinit var currencyCode: String

    constructor()

    @JvmOverloads
    constructor(denomination: BigDecimal, currency: Currency = DEFAULT_CURRENCY) : this(denomination, currency.currencyCode)

    @JvmOverloads
    constructor(denomination: Double, currency: Currency = DEFAULT_CURRENCY) : this(BigDecimal(denomination), currency.currencyCode)

    constructor(denomination: Double, currencyCode: String) : this(BigDecimal(denomination), currencyCode)

    private constructor(denomination: BigDecimal, currencyCode: String) {
        this.denomination = denomination.setScale(2, RoundingMode.HALF_EVEN)
        this.currencyCode = currencyCode
    }

    fun multiplyBy(multiplier: Double): Money {
        return multiplyBy(BigDecimal(multiplier))
    }

    fun multiplyBy(multiplier: BigDecimal): Money {
        return Money(denomination.multiply(multiplier), currencyCode)
    }

    fun add(money: Money): Money {
        checkCurrencyCompatibility(money)
        return Money(denomination.add(money.denomination), currencyCode)
    }

    fun subtract(money: Money): Money {
        checkCurrencyCompatibility(money)
        return Money(denomination.subtract(money.denomination), currencyCode)
    }

    fun greaterThan(other: Money): Boolean {
        return denomination.compareTo(other.denomination) > 0
    }

    fun lessThan(other: Money): Boolean {
        return denomination.compareTo(other.denomination) < 0
    }

    fun hasCompatibleCurrency(money: Money): Boolean {
        checkCurrencyCompatibility(money)
        return true
    }

    private fun checkCurrencyCompatibility(money: Money) {
        if (incompatibleCurrency(money)) {
            throw IllegalArgumentException("Currency mismatch : " + currencyCode + " -> " + money.currencyCode)
        }
    }

    private fun incompatibleCurrency(money: Money): Boolean {
        return currencyCode != money.currencyCode
    }

    override fun toString(): String {
        return String.format(Locale.ENGLISH, "%0$.2f %s", denomination, currencyCode)

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Money

        if (denomination != other.denomination) return false
        if (currencyCode != other.currencyCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = denomination.hashCode()
        result = 31 * result + currencyCode.hashCode()
        return result
    }


    companion object {
        @JvmField
        val DEFAULT_CURRENCY: Currency = Currency.getInstance("PLN")
        @JvmField
        val ZERO = Money(BigDecimal.ZERO)
    }
}