package com.javacaptain.video.rental.store.rental


import com.javacaptain.video.rental.store.common.IntegrationTest
import com.javacaptain.video.rental.store.rental.config.BonusPointTestListener
import com.javacaptain.video.rental.store.rental.config.StubConfig
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Shared

@Import(StubConfig.class)
class RentalIT extends IntegrationTest {
    @LocalServerPort
    String port

    @Shared
    WebClient webClient = WebClient.create()

    @Shared
    def jsonSlurper = new JsonSlurper()

    @Autowired
    BonusPointTestListener bonusPointTestListener

    def "Should create rental and return"() {

        given: "Create rental request"
        def request = getClass().getResource("create_rental_request.json").text

        when: "Request has been submitted"
        def response = webClient.post()
                .uri("http://localhost:${port}/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toEntity(String)
                .block()

        then: "Status code is 2xx"
        response.statusCode.is2xxSuccessful()

        and: "Response is parsed"
        def rentalResponse = jsonSlurper.parseText(response.body)

        and: "Total price is correct"
        rentalResponse["totalPrice"]["denomination"] == 20.00
        rentalResponse["totalPrice"]["currencyCode"] == "USD"

        when: "Return request has been submitted"
        def returnResponse = webClient.post()
                .uri("http://localhost:${port}/rentals/${rentalResponse["rentalId"]}/returns")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String)
                .block()

        then: "Status code is 2xx"
        returnResponse.statusCode.is2xxSuccessful()

        and: "Message has been published to bonus point listener"
        bonusPointTestListener.received.size() == 1
    }

}