package pl.zielichowski.rentalstore.inventory.it

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import pl.zielichowski.rentalstore.common.api.inventory.InventoryCreatedEvent
import pl.zielichowski.rentalstore.inventory.query.InventoryHandler
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class InventoryTestIT extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private InventoryHandler inventoryHandler

    @Shared
    def jsonSlurper = new JsonSlurper()

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    def "should create inventory with movies"(){
        given: "test data"
        def request = getClass().getResource("create_inventory_request.json").text

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .post("/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andReturn()
                .response

        then: "status is 201"
        response.status == 201

        and: "Location header is not empty"
        def header = response.getHeader("Location")
        !header.isEmpty()
    }

    def "should get inventories view"() {
        given: "test data"
        def movies = createMovies()
        def inventoryCreatedEvent = new InventoryCreatedEvent("1", movies)
        inventoryHandler.on(inventoryCreatedEvent)

        and: "Expected response body"
        def expectedResponseBody = jsonSlurper.parse(getClass().getResource("inventories_response.json"))

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/inventories")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .response

        then: "response status is 200"
        response.status == 200

        and: "Response body is correct"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponseBody
    }

    def "should get inventory details view"() {
        given: "test data"
        def movies = createMovies()
        def inventoryCreatedEvent = new InventoryCreatedEvent("1", movies)
        inventoryHandler.on(inventoryCreatedEvent)

        and: "Expected response body"
        def expectedResponseBody = jsonSlurper.parse(getClass().getResource("inventories_details_response.json"))

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/inventories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .response

        then: "response status is 200"
        response.status == 200

        and: "Response body is correct"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponseBody
    }

    def "should get movies in inventory"() {
        given: "test data"
        def movies = createMovies()
        def inventoryCreatedEvent = new InventoryCreatedEvent("1", movies)
        inventoryHandler.on(inventoryCreatedEvent)

        and: "Expected response body"
        def expectedResponseBody = jsonSlurper.parse(getClass().getResource("movies_response.json"))

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/movies")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .response

        then: "response status is 200"
        response.status == 200

        and: "Response body is correct"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponseBody
    }

    def "should get movie details"() {
        given: "test data"
        def movies = createMovies()
        def inventoryCreatedEvent = new InventoryCreatedEvent("1", movies)
        inventoryHandler.on(inventoryCreatedEvent)

        and: "Expected response body"
        def expectedResponseBody = jsonSlurper.parse(getClass().getResource("movie_details_response.json"))

        when: "Request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/movies/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .response

        then: "response status is 200"
        response.status == 200

        and: "Response body is correct"
        def responseBody = jsonSlurper.parseText(response.contentAsString)
        responseBody == expectedResponseBody
    }

    Map createMovies() {
        def movies = [:]
        movies.put(new MovieId("2"), new MovieInfo("Title2", MovieTypeName.OLD))

        return movies
    }
}
