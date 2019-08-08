package pl.zielichowski.rentalstore.rental.domain

import pl.zielichowski.rentalstore.common.api.domain.Money
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import spock.lang.Specification
import spock.lang.Unroll

class MovieTypeTest extends Specification {
    @Unroll
    def 'should return bonus points'(MovieTypeName movieTypeName, Integer expectedPoints) {
        given:
        MovieType printable = MovieType.@Companion.of(movieTypeName)

        when:

        def points = printable.calculateBonusPoints()

        then:
        points == expectedPoints

        where:
        movieTypeName         | expectedPoints
        MovieTypeName.NEW     | 2
        MovieTypeName.REGULAR | 1
        MovieTypeName.OLD     | 1
    }

    @Unroll
    def 'should return expected surcharge'(MovieTypeName movieTypeName, Integer daysOfDelay, Money expectedSurcharge) {
        given:
        MovieType movieType = MovieType.@Companion.of(movieTypeName)

        when:
        def surcharge = movieType.calculateSurcharge(daysOfDelay)
        then:
        surcharge == expectedSurcharge

        where:
        movieTypeName         | daysOfDelay | expectedSurcharge
        MovieTypeName.NEW     | 1           | new Money(40.00, "SEK")
        MovieTypeName.REGULAR | 1           | new Money(30.00, "SEK")
        MovieTypeName.OLD     | 1           | new Money(30.00, "SEK")
        MovieTypeName.NEW     | 2           | new Money(80.00, "SEK")
        MovieTypeName.REGULAR | 2           | new Money(60.00, "SEK")
        MovieTypeName.OLD     | 2           | new Money(60.00, "SEK")
        MovieTypeName.NEW     | 4           | new Money(160.00, "SEK")
        MovieTypeName.REGULAR | 4           | new Money(120.00, "SEK")
        MovieTypeName.OLD     | 4           | new Money(120.00, "SEK")
    }

    @Unroll
    def 'should return calculated price'(MovieTypeName movieTypeName, Integer daysOfRental, Money expectedPrice) {
        given:
        MovieType movieType = MovieType.@Companion.of(movieTypeName)

        when:
        def price = movieType.calculatePrice(daysOfRental)
        then:
        price == expectedPrice

        where:
        movieTypeName         | daysOfRental | expectedPrice
        MovieTypeName.NEW     | 1            | new Money(40.00, "SEK")
        MovieTypeName.REGULAR | 1            | new Money(30.00, "SEK")
        MovieTypeName.OLD     | 1            | new Money(30.00, "SEK")
        MovieTypeName.NEW     | 3            | new Money(120.00, "SEK")
        MovieTypeName.REGULAR | 3            | new Money(30.00, "SEK")
        MovieTypeName.OLD     | 3            | new Money(30.00, "SEK")
        MovieTypeName.NEW     | 6            | new Money(240.00, "SEK")
        MovieTypeName.REGULAR | 6            | new Money(120.00, "SEK")
        MovieTypeName.OLD     | 6            | new Money(60.00, "SEK")
    }
}
