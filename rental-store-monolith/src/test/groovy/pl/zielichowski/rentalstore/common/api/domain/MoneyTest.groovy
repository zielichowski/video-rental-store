package pl.zielichowski.rentalstore.common.api.domain

import spock.lang.Specification

class MoneyTest extends Specification {
    def 'should multiply money'() {
        given:
        def money = new Money(10.00, "PLN")
        when:
        def result = money.multiplyBy(2)
        then:
        result == new Money(20.00, "PLN")
    }

    def 'should add money'() {
        given:
        def money = new Money(10.00, "PLN")
        when:
        def result = money.add(new Money(5, "PLN"))
        then:
        result == new Money(15, "PLN")
    }

    def 'should subtract money'() {
        given:
        def money = new Money(10.00, "PLN")
        when:
        def result = money.subtract(new Money(5, "PLN"))
        then:
        result == new Money(5, "PLN")
    }

    def 'should choose greater money'() {
        given:
        def ten = new Money(10.00, "PLN")
        def fifteen = new Money(15.00, "PLN")
        when:
        def greaterThan = fifteen.greaterThan(ten)
        then:
        greaterThan
    }

    def 'should choose less money'() {
        given:
        def ten = new Money(10.00, "PLN")
        def fifteen = new Money(15.00, "PLN")
        when:
        def lessThan = ten.lessThan(fifteen)
        then:
        lessThan
    }

    def 'should throw exception on incompatible currency'() {
        given:
        def tenPln = new Money(10.00, "PLN")
        def tenEur = new Money(10.00, "EUR")

        when:
        tenEur.hasCompatibleCurrency(tenPln)

        then:
        thrown(IllegalArgumentException)
    }

    def 'should not throw exception on compatible currency'() {
        given:
        def tenPln = new Money(10.00, "PLN")
        def twoPln = new Money(2.00, "PLN")
        when:
        twoPln.hasCompatibleCurrency(tenPln)
        then:
        noExceptionThrown()
    }
}
