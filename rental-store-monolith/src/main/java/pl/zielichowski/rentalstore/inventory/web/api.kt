package pl.zielichowski.rentalstore.inventory.web

import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName

data class CreateInventoryRequest(val movies: List<Movie>)

data class Movie(val title: String, val type: MovieTypeName)