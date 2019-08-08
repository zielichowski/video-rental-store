package pl.zielichowski.rentalstore.common.api.rental

import org.axonframework.modelling.command.TargetAggregateIdentifier
import pl.zielichowski.rentalstore.common.api.domain.Money
import pl.zielichowski.rentalstore.common.api.domain.MovieId
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName
import java.time.LocalDate

data class RentalItem(val movieId: MovieId, val movieTypeName: MovieTypeName, val daysOfRental: Int)

data class RentedMovie(val movieId: MovieId, val daysOfRental: Int, val price: Money)
data class RentedMovies(val rentedMovies: List<RentedMovie>, val totalPrice: Money)

data class SubmitRentalCommand(@TargetAggregateIdentifier val rentalId: String, val inventoryId: String, val userId: String, val rentalItems: List<RentalItem>, val date: LocalDate)
data class RentalSubmittedEvent(val rentalId: String, val inventoryId: String, val userId: String, val rentalItems: List<RentalItem>, val date: LocalDate)

data class RejectRentalCommand(@TargetAggregateIdentifier val rentalId: String)
data class RentalRejectedEvent(val rentalId: String, val userId: String)

data class ReturnRentalItemCommand(@TargetAggregateIdentifier val rentalId: String, val movieId: MovieId, val returnDate: LocalDate)
data class RentalItemReturnedEvent(val rentalId: String, val movieId: MovieId, val returnDate: LocalDate)

data class AcceptRentalCommand(@TargetAggregateIdentifier val rentalId: String)
data class RentalAcceptedEvent(@TargetAggregateIdentifier val rentalId: String, val userId: String, val date: LocalDate, val movies: RentedMovies)

data class RentalFinishedEvent(val rentalId: String, val totalSurcharge: Money)

data class BonusPointsCalculatedEvent(val rentalId: String, val userId: String, val bonusPoints: Int)

data class CalculatePossibleSurchargesCommand(@TargetAggregateIdentifier val rentalId: String, val movieId: MovieId, val returnDate: LocalDate)
data class SurchargeCalculatedEvent(val rentalId: String, val movieId: MovieId, val surcharge: Money)
