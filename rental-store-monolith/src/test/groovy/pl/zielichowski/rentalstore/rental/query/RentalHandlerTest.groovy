package pl.zielichowski.rentalstore.rental.query

import pl.zielichowski.rentalstore.common.api.domain.Money
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.rental.PublicRentalStatus
import pl.zielichowski.rentalstore.common.api.rental.RentalAcceptedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentalFinishedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentalItemReturnedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentalItemStatus
import pl.zielichowski.rentalstore.common.api.rental.RentalRejectedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentedMovie
import pl.zielichowski.rentalstore.common.api.rental.RentedMovies
import pl.zielichowski.rentalstore.common.api.rental.SurchargeCalculatedEvent
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

class RentalHandlerTest extends Specification {

    private RentalHandler testSubject;

    def rentalViewRepository = Mock(RentalViewRepository)
    def rentalItemViewRepository = Mock(RentalItemViewRepository)

    @Shared
    def rentalId = "rentalId1"
    @Shared
    def userId = "userId1"
    @Shared
    def price = new Money(10.00)
    @Shared
    def daysOfRental = 2
    @Shared
    def rentalItemView = createRentalItemView()
    @Shared
    def rentalView = createRentalView()

    def "should save rental view"() {
        given: "test data"
        def rentedMovies = new RentedMovies([new RentedMovie(new MovieId("1"), daysOfRental, price)], price)
        def rentalAcceptedEvent = new RentalAcceptedEvent(rentalId, userId, LocalDate.now(), rentedMovies)
        def expectedView = rentalView

        when: "event is handled"
        testSubject.on(rentalAcceptedEvent)

        then: "rental view is saved"
        1 * rentalViewRepository.save(_) >> { RentalView rv ->
            verifyAll(rv) {
                rv.userId == expectedView.userId
                rv.rentalId == expectedView.rentalId
                rv.rentalStatus == expectedView.rentalStatus
                rv.totalPrice == expectedView.totalPrice
                rv.rentalItemViews == expectedView.rentalItemViews
            }
        }

    }

    def "should update rental item status"() {
        given: "test data"
        def itemReturnedEvent = new RentalItemReturnedEvent(rentalId, new MovieId("1"), LocalDate.now())

        when: "event is handled"
        testSubject.on(itemReturnedEvent)

        then: "repository is called"
        1 * rentalViewRepository.findById(rentalId) >> Optional.of(rentalView)

        and: "rental item status is updated"
        rentalView.rentalItemViews.find({ item -> item.itemId == "1" }).rentalItemStatus == RentalItemStatus.RETURNED
    }

    def "should create rejected rental view"() {
        given: "test data"
        def rentalRejectedEvent = new RentalRejectedEvent(rentalId, userId)
        def expectedView = RentalView.builder().userId(userId).rentalId(rentalId).rentalStatus(PublicRentalStatus.REJECTED)

        when: "event is handled"
        testSubject.on(rentalRejectedEvent)

        then: "rental view is created"
        1 * rentalViewRepository.save(_) >> {
            RentalView rv ->
                verifyAll(rv) {
                    rv.userId == expectedView.userId
                    rv.rentalId == expectedView.rentalId
                    rv.rentalStatus == expectedView.rentalStatus
                }
        }
    }

    def "should update view on rental finished event"() {
        given: "test data"
        def rentalFinishedEvent = new RentalFinishedEvent(rentalId, price)
        def expectedView = getExpectedView()

        when: "event is handled"
        testSubject.on(rentalFinishedEvent)

        then: "rental view is found"
        1 * rentalViewRepository.findById(rentalId) >> Optional.of(rentalView)

        and: "rental view is correct"
        rentalView == expectedView
    }

    def "should add surcharge to view"() {
        given: "test data"
        def surchargeCalculatedEvent = new SurchargeCalculatedEvent(rentalId, new MovieId("1"), price)

        when: "event is handled"
        testSubject.on(surchargeCalculatedEvent)

        then: "view is found"
        1 * rentalViewRepository.findById(rentalId) >> Optional.of(rentalView)

        and: "surcharge is added"
        rentalView.rentalItemViews.find({ item -> item.itemId == "1" }).surcharge == price.toString()
    }

    RentalView getExpectedView() {
        def view = createRentalView()
        view.setTotalSurcharge(price.toString())
        view.setRentalStatus(PublicRentalStatus.FINISHED)
        return view
    }

    RentalView createRentalView() {
        return RentalView.builder()
                .userId(userId)
                .rentalId(rentalId)
                .rentalStatus(PublicRentalStatus.ACCEPTED)
                .totalPrice(price.toString())
                .rentalItemViews([rentalItemView])
                .build()
    }

    RentalItemView createRentalItemView() {
        return RentalItemView.builder()
                .daysOfRental(daysOfRental)
                .userId(userId)
                .itemId("1")
                .price(price.toString())
                .rentalItemStatus(RentalItemStatus.RENTED)
                .build();
    }

    def setup() {
        testSubject = new RentalHandler(rentalViewRepository, rentalItemViewRepository)
    }
}
