package pl.zielichowski.rentalstore.points.query

import pl.zielichowski.rentalstore.common.api.rental.BonusPointsCalculatedEvent
import spock.lang.Shared
import spock.lang.Specification

class BonusPointsEventHandlerTest extends Specification {

    private BonusPointsEventHandler testSubject;
    def bonusPointsRepository = Mock(BonusPointsViewRepository)

    @Shared
    def rentalId = "rentalId1"
    @Shared
    def userId = "userId1"

    def setup() {
        testSubject = new BonusPointsEventHandler(bonusPointsRepository)
    }

    def "should create bonus points view"() {
        given: "test data"
        def pointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)
        def expectedView = new BonusPointsView(userId, 3)

        when: "event is handled"
        testSubject.on(pointsCalculatedEvent)

        then: "repository view not exists"
        1 * bonusPointsRepository.findById(userId) >> Optional.empty()

        and: "view is created"
        1 * bonusPointsRepository.save(_) >> { BonusPointsView pointsView ->
            verifyAll(pointsView) {
                pointsView.userId == expectedView.userId
                pointsView.bonusPoints == expectedView.bonusPoints
            }
        }
    }

    def "should add bonus points to existing view"() {
        given: "test data"
        def pointsCalculatedEvent = new BonusPointsCalculatedEvent(rentalId, userId, 3)
        def bonusPointsView = new BonusPointsView(userId, 2)
        def expectedView = new BonusPointsView(userId, 5)

        when: "event is handled"
        testSubject.on(pointsCalculatedEvent)

        then: "existing view is returned"
        1 * bonusPointsRepository.findById(userId) >> Optional.of(bonusPointsView)

        and: "points are added"
        1 * bonusPointsRepository.save(_) >> { BonusPointsView pointsView ->
            verifyAll(pointsView) {
                pointsView.userId == expectedView.userId
                pointsView.bonusPoints == expectedView.bonusPoints
            }
        }
    }
}
