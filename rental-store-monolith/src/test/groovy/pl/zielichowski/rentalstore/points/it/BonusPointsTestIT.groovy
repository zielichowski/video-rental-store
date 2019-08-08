package pl.zielichowski.rentalstore.points.it

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.zielichowski.rentalstore.common.api.rental.BonusPointsCalculatedEvent
import pl.zielichowski.rentalstore.points.query.BonusPointsEventHandler
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class BonusPointsTestIT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private BonusPointsEventHandler bonusPointsHandler

    @Shared
    def jsonSlurper = new JsonSlurper()

    def "should get bonus points view"() {
        given: "test data"
        def pointsCalculatedEvent = new BonusPointsCalculatedEvent("rentalId1", "tomasz", 3)
        bonusPointsHandler.on(pointsCalculatedEvent)

        and: "expected response body"
        def expectedResponseBody = jsonSlurper.parse(getClass().getResource("bonus_points_response.json"))

        when: "request is submitted"
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/points")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Api-key", "tomasz"))
                .andReturn()
                .response

        then: "response status is 200"
        response.status == 200

        and: "response body is correct"
        def body = jsonSlurper.parseText(response.contentAsString)
        expectedResponseBody == body
    }
}