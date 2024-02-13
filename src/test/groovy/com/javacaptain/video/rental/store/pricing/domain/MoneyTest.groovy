package com.javacaptain.video.rental.store.pricing.domain

import com.javacaptain.video.rental.store.common.Money
import spock.lang.Specification

class MoneyTest extends Specification {
    def 'should multiply money'() {
        given:
        def money = new Money(10.00, "USD")
        when:
        def result = money.multiplyBy(2)
        then:
        result == new Money(20.00, "USD")
    }

    def 'should add money'() {
        given:
        def money = new Money(10.00, "USD")
        when:
        def result = money.add(new Money(5, "USD"))
        then:
        result == new Money(15, "USD")
    }


    def 'should throw exception on incompatible currency'() {
        given:
        def tenUSD = new Money(10.00, "USD")
        def tenEur = new Money(10.00, "EUR")

        when:
        tenEur.add(tenUSD)

        then:
        thrown(IllegalArgumentException)
    }

}
