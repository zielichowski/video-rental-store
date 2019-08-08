package pl.zielichowski.rentalstore.rental.web

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


class RentalItemRequest {
    @NotBlank
    @NotNull
    lateinit var movieId: String
    @NotNull
    var daysOfRental: Int? = null
}

class RentalItemsRequest {
    @Valid
    lateinit var listOfItems: List<RentalItemRequest>
}

class CreateRentalRequest {
    @NotBlank
    lateinit var inventoryId: String
    @Valid
    lateinit var rentalItemsRequest: RentalItemsRequest
}

class ReturnItemRequest {
    @NotBlank
    lateinit var movieId: String
}

class ReturnItemsRequest {
    @Valid
    lateinit var listOfItems: List<ReturnItemRequest>
}
