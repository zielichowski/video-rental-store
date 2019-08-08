package pl.zielichowski.rentalstore.rental.it

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.zielichowski.rentalstore.common.api.domain.Money
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.rental.RentalAcceptedEvent
import pl.zielichowski.rentalstore.common.api.rental.RentedMovie
import pl.zielichowski.rentalstore.common.api.rental.RentedMovies
import pl.zielichowski.rentalstore.rental.query.RentalHandler
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
class RentalTestIT extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private RentalHandler rentalHandler

    @Shared
    def jsonSlurper = new JsonSlurper()

    def "Should get rentals"() {
        given: "Event caught by handler"
        def event =
                new RentalAcceptedEvent("1",
                        "tomasz",
                        LocalDate.now(),
                        new RentedMovies(
                                [new RentedMovie(new MovieId("1"),
                                        5,
                                        new Money(10.00))],
                                new Money(10.00)))
        rentalHandler.on(event)

        and: "Expected response body"
        def expectedResponseBody = jsonSlurper.parse(getClass().getResource("rental_response.json"))

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/rentals")
                .header("Api-key", "tomasz")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .response

        then: "Status code is 200"
        response.status == 200

        and: "Response body is correct"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponseBody
    }

    def "Should get rental details by id"() {
        given: "Event caught by handler"
        def rentalId = "1"
        def event =
                new RentalAcceptedEvent(rentalId,
                        "tomasz",
                        LocalDate.now(),
                        new RentedMovies(
                                [new RentedMovie(new MovieId("1"),
                                        5,
                                        new Money(10.00))],
                                new Money(10.00)))
        rentalHandler.on(event)

        and: "Expected response body"
        def expectedResponseBody = jsonSlurper.parse(getClass().getResource("rental_response_one_element.json"))

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/rentals/${rentalId}")
                .header("Api-key", "tomasz")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .response

        then: "Status code is 200"
        response.status == 200

        and: "Response body is correct"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponseBody
    }

    def "should rent movie"() {
        given: "Rental request"
        def request = getClass().getResource("rental_request.json").text

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .post("/rentals")
                .header("Api-key", "tomasz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andReturn()
                .response

        then: "Status code is 201"
        response.status == 201

        and: "Location header is not empty"
        def header = response.getHeader("Location")
        !header.isEmpty()
    }

    def "should return 400 when api-key is missing"() {
        given: "Rental request"
        def request = getClass().getResource("rental_request.json").text
        def expectedResponse = jsonSlurper.parse(getClass().getResource("missing_api_key_response.json"))

        when: "Request is submitted without api-key"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andReturn()
                .response

        then: "Status code is 400"
        response.status == 400

        and: "Message is well formatted"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponse
    }

    def "should return 400 when required field is missing"() {
        given: "Rental request"
        def request = getClass().getResource("missing_required_field_request.json").text
        def expectedResponse = jsonSlurper.parse(getClass().getResource("missing_required_field_response.json"))

        when: "Request is submitted without api-key"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Api-key", "tomasz")
                .content(request))
                .andReturn()
                .response

        then: "Status code is 400"
        response.status == 400

        and: "Message is well formatted"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponse
    }

    def "should return movie"() {
        given: "Return items"
        def request = getClass().getResource("return_request.json").text
        def testRentalId = UUID.randomUUID().toString()

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .post("/rentals/${testRentalId}/returns")
                .header("Api-key", "tomasz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andReturn()
                .response

        then: "Status code is 202"
        response.status == 202
    }
}
