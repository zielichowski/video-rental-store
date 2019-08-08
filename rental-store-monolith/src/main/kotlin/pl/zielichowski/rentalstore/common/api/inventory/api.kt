package pl.zielichowski.rentalstore.common.api.inventory

import org.axonframework.modelling.command.TargetAggregateIdentifier
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName

//Commands & Events
data class CreateInventoryCommand(@TargetAggregateIdentifier val inventoryId: String, val movies: Map<MovieId, MovieInfo>)

data class InventoryCreatedEvent(val inventoryId: String, val movies: Map<MovieId, MovieInfo>)

data class ReturnMovieCommand(@TargetAggregateIdentifier val inventoryId: String, val movieId: MovieId)
data class MovieReturnedEvent(val inventoryId: String, val movieId: MovieId)

data class CreateInventoryOrderCommand(@TargetAggregateIdentifier val inventoryOrderId: String, val inventoryId: String, val movies: List<MovieId>)
data class InventoryOrderCreatedEvent(val inventoryOrderId: String, val inventoryId: String, val movies: List<MovieId>)

data class InventoryOrderValidatedWithSuccessEvent(val inventoryId: String, val inventoryOrderId: String, val movies: List<MovieId>)
data class InventoryOrderValidatedWithErrorEvent(val inventoryId: String, val inventoryOrderId: String)

data class MovieData(val movieId: MovieId, val movieType: MovieTypeName)

enum class PublicMovieStatus {
    AVAILABLE, RENTED
}

class MovieNotFoundException(movieId: String) : RuntimeException(String.format("Movie not found for given id {movieId=%s}", movieId))
