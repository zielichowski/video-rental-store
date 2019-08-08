package pl.zielichowski.rentalstore


import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

import static org.awaitility.Awaitility.await
import static org.hamcrest.number.OrderingComparison.greaterThan

class RentalTest extends Specification {
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
        await().atMost(1, TimeUnit.SECONDS).until(moviesAdded(inventoryResourceAddress), greaterThan(0))

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
        await().atMost(1, TimeUnit.SECONDS).until(rentalsAdded(rentalResourceAddress), greaterThan(0))

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
        await().atMost(1, TimeUnit.SECONDS).until(rentalIsFinished(rentalResourceAddress))
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

    Callable<Object> moviesAdded(resourceAddress) {
        Callable<Object> response = {
            def response = restClient.get(uri: resourceAddress + "/movies", contentType: ContentType.JSON)
            return response.data["_embedded"]["movies"].size()
        }
        return response
    }

    Callable<Object> rentalsAdded(resourceAddress) {
        Callable<Object> response = {
            def response = restClient.get(uri: resourceAddress, contentType: ContentType.JSON, headers: ["Api-key": apiKey])
            return response.data["items"].size()
        }
        return response
    }

    Callable<Boolean> rentalIsFinished(rentalResourceAddress) {
        Callable<Boolean> response = {
            def rentalData = restClient.get(uri: rentalResourceAddress, contentType: ContentType.JSON, headers: ["Api-key": apiKey])
            return rentalData.data.rentalStatus == "FINISHED" && rentalData.data.totalSurcharge == "0.00 SEK"
        }
    }
}
