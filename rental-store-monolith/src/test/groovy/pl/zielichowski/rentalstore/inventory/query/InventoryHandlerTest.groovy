package pl.zielichowski.rentalstore.inventory.query

import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import pl.zielichowski.rentalstore.common.api.inventory.InventoryCreatedEvent
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent
import pl.zielichowski.rentalstore.common.api.inventory.MovieData
import pl.zielichowski.rentalstore.common.api.inventory.MovieReturnedEvent
import pl.zielichowski.rentalstore.common.api.inventory.PublicMovieStatus
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors

class InventoryHandlerTest extends Specification {

    private InventoryHandler testSubject;
    def inventoryViewRepository = Mock(InventoryViewRepository)
    def movieViewRepository = Mock(MovieViewRepository)

    @Shared
    def inventoryId = "inventoryId1"

    @Shared
    def inventoryOrderId = "inventoryOrderId1"

    @Shared
    def movies = [:]

    @Shared
    def movieEntities = []

    def setup() {
        testSubject = new InventoryHandler(inventoryViewRepository, movieViewRepository)
        movies.put(new MovieId("123"), new MovieInfo("Title1", MovieTypeName.NEW))
        movies.put(new MovieId("456"), new MovieInfo("Title2", MovieTypeName.REGULAR))
        movieEntities =
                movies
                        .entrySet()
                        .stream()
                        .map({ entry -> new MovieView(entry.getKey().getIdentifier(), entry.getValue(), PublicMovieStatus.AVAILABLE) })
                        .collect(Collectors.toSet())
    }

    def "should save inventory view"() {
        given: "test data"
        def inventoryCreatedEvent = new InventoryCreatedEvent(inventoryId, movies)
        def inventoryView = new InventoryView(inventoryId, movieEntities as Set<MovieView>)

        when: "event is handled"
        testSubject.on(inventoryCreatedEvent)

        then: "correct data is saved"
        1 * inventoryViewRepository.save(_) >> { InventoryView iv ->
            verifyAll(iv) {
                iv.inventoryId == inventoryView.inventoryId
                iv.movieEntities == inventoryView.movieEntities
            }
        }
    }

    def "should update movie status to rented"() {
        given: "test data"
        def withSuccessEvent = new InventoryOrderValidatedWithSuccessEvent(inventoryId, inventoryOrderId, movies.keySet().asList())

        when: "event is handled"
        testSubject.on(withSuccessEvent)

        then: "repository is called"
        withSuccessEvent
                .movies
                .forEach({ movieId ->
                    1 * movieViewRepository.findById(movieId.identifier) >> Optional.of(movieEntities.find {
                        it.movieId == movieId.identifier
                    })
                })

        and: "movies are rented"
        movieEntities
                .forEach({ movie -> movie.movieStatus == PublicMovieStatus.RENTED })
    }

    def "should update movie status to available"() {
        given:
        def movieReturnedEvent = new MovieReturnedEvent(inventoryId, new MovieId("123"))
        def movieView = movieEntities.find { it.movieId == "123" }

        when:
        testSubject.on(movieReturnedEvent)

        then:
        1 * movieViewRepository.findById("123") >> Optional.of(movieView)
        movieView.movieStatus == PublicMovieStatus.AVAILABLE
    }

    def "should find movie data"() {
        given:
        def dataQuery = new FindMoviesDataInInventory(inventoryId, ["123", "456"])
        def expectedMovieData =
                [new MovieData(new MovieId("123"), MovieTypeName.NEW), new MovieData(new MovieId("456"), MovieTypeName.REGULAR)]
        when:
        def movieData = testSubject.on(dataQuery)
        then:
        dataQuery
                .moviesIds
                .forEach({ movieId ->
                    1 * movieViewRepository.findById(movieId) >> Optional.of(movieEntities.find {
                        it.movieId == movieId
                    })
                })
        movieData == expectedMovieData
    }
}
