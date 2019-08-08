package pl.zielichowski.rentalstore.inventory.domain

import org.axonframework.modelling.command.TargetAggregateIdentifier
import pl.zielichowski.rentalstore.common.api.domain.MovieId


internal enum class MovieStatus {
    AVAILABLE, RENTED
}

internal data class ValidateInventoryOrderCommand(@TargetAggregateIdentifier val inventoryId: String, val inventoryOrderId: String, val movies: List<MovieId>)