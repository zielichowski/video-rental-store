package com.javacaptain.video.rental.store.inventory


import com.javacaptain.video.rental.store.common.IntegrationTest
import com.javacaptain.video.rental.store.common.MovieType
import com.javacaptain.video.rental.store.inventory.api.MovieView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Shared

@Import(InventoryStubConfig.class)
class InventoryIT extends IntegrationTest {

    @LocalServerPort
    String port

    @Shared
    WebClient webClient = WebClient.create()

    @Autowired
    BonusPointTestListener bonusPointEventListener

    @Autowired
    PricingTestListener pricingEventListener


    def "Should be able to store and read movie"() {

        given: "Create movie request"
        def request = getClass().getResource("create_inventory_request.json").text

        when: "Request has been submitted"
        def response = webClient.post()
                .uri("http://localhost:${port}/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toEntity(String)
                .block()

        then: "Movie location is returned"
        def movieLocationUrl = response.headers["Location"][0]

        when: "Getting movie"
        def movie = webClient.get()
                .uri("http://localhost:${port}${movieLocationUrl}")
                .retrieve()
                .bodyToMono(MovieView)
                .block()

        then: "Correct movie is returned"
        movie.movieType() == MovieType.REGULAR.name()
        movie.movieTitle() == "Spider Man"

        and: "Message has been published to bonus point listener"
        bonusPointEventListener.received.size() == 1

        and: "Message has been published to pricing listener"
        pricingEventListener.received.size() == 1
    }
}
