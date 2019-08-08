package pl.zielichowski.rentalstore

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class RentalProcessTest extends Specification {
    @Shared
    def port = 8080
    @Shared
    def restClient = new RESTClient()
    @Shared
    def jsonSlurper = new JsonSlurper()

    @Shared
    def apiKey = "tomasz"

    @Shared
    def rentalSettings = [:]

    @Shared
    def rentedMovies = []

    @Shared
    def receiveCondition = new PollingConditions(delay: 0.2, initialDelay: 0.2, timeout: 3)


    def setup() {
        rentalSettings.put("Out of Africa", 7)
        rentalSettings.put("Matrix 11", 1)
        rentalSettings.put("Spider Man 2", 2)
        rentalSettings.put("Spider Man", 5)
    }

    def "should walk through rental process"() {

        given: "request to create inventory with movies"
        def inventoryRequest = jsonSlurper.parseText(getClass().getResource("create_inventory_request.json").text)

        when: "movies added to inventory"
        def inventoriesResponse = restClient.post(uri: "http://localhost:${port}/inventories", body: inventoryRequest, headers: ["Content-Type": "application/json"], requestContentType: ContentType.JSON)
        def inventoryResourceAddress = inventoriesResponse.headers.location

        then: "wait until view model is updated"
        receiveCondition.eventually {
            def response = restClient.get(uri: inventoryResourceAddress + "/movies", contentType: ContentType.JSON)
            assert response.data["_embedded"]["movies"].size() > 0
        }

        when: "read movie data"
        def inventoryResponse = restClient.get(uri: inventoryResourceAddress + "/movies", contentType: ContentType.JSON)

        and: "request to post rental is created"
        def rentalRequest = createRentalRequest(inventoryResourceAddress, inventoryResponse)

        then: "rental request is posted"
        def rentalResponse = restClient.post(
                uri: "http://localhost:${port}/rentals",
                body: rentalRequest,
                headers: ["Content-Type": "application/json", "Api-key": apiKey], requestContentType: ContentType.JSON)
        def rentalResourceAddress = rentalResponse.headers.location

        and: "wait until rental view model is updated"
        receiveCondition.eventually {
            def response = restClient.get(uri: rentalResourceAddress, contentType: ContentType.JSON, headers: ["Api-key": apiKey])
            assert response.status == 200
        }

        when: "get rental data"
        def rentalDataResponse = restClient.get(uri: rentalResourceAddress, contentType: ContentType.JSON, headers: ["Api-key": apiKey])

        then: "price is 250.00 SEK"
        rentalDataResponse.data.totalPrice == "250.00 SEK"

        when: "post return movies"
        def rentalId = rentalResourceAddress.tokenize('/').last()
        def returnRequestBody = createReturnRequest()

        def returnResponse = restClient.post(
                uri: "http://localhost:${port}/rentals/${rentalId}/returns",
                body: returnRequestBody,
                headers: ["Content-Type": "application/json", "Api-key": apiKey], requestContentType: ContentType.JSON)

        then: "rental status is FINISHED and surcharge is 0 SEK"
        returnResponse.status == 202
        receiveCondition.eventually {
            def rentalData = restClient.get(uri: rentalResourceAddress, contentType: ContentType.JSON, headers: ["Api-key": apiKey])
            assert rentalData.data.rentalStatus == "FINISHED" && rentalData.data.totalSurcharge == "0.00 SEK"
        }
    }

    Object createReturnRequest() {
        def parsedReturnRequest = jsonSlurper.parse(getClass().getResource("return_rentals_request.json"))
        for (int i = 0; i < rentedMovies.size(); i++) {
            parsedReturnRequest.listOfItems.get(i).movieId = rentedMovies[i]
        }
        return parsedReturnRequest
    }

    private Object createRentalRequest(resourceAddress, inventoryResponse) {
        def parsedRentalRequest = jsonSlurper.parse(getClass().getResource("create_rental_request.json"))
        def inventoryId = resourceAddress.tokenize('/').last()
        parsedRentalRequest.inventoryId = inventoryId
        for (int i = 0; i < inventoryResponse.data["_embedded"]["movies"].size(); i++) {
            String movieId = inventoryResponse.data["_embedded"]["movies"]["_links"]["self"]["href"].get(i).tokenize('/').last()
            rentedMovies.add(movieId)
            String movieTitle = inventoryResponse.data["_embedded"]["movies"].get(i)["movieInfo"].title
            String daysOfRental = rentalSettings.get(movieTitle)
            parsedRentalRequest.rentalItemsRequest.listOfItems.get(i).movieId = movieId
            parsedRentalRequest.rentalItemsRequest.listOfItems.get(i).daysOfRental = daysOfRental
        }
        return parsedRentalRequest

    }
}
