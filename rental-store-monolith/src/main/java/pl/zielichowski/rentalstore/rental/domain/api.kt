package pl.zielichowski.rentalstore.rental.domain

import org.axonframework.modelling.command.TargetAggregateIdentifier
import pl.zielichowski.rentalstore.common.api.domain.Money

internal enum class RentalStatus {
    CREATED, REJECTED, ACCEPTED, FINISHED
}

//Commands & Events
internal data class RentalConfirmedEvent(val rentalId: String)

internal data class PriceCalculatedEvent(val rentalId: String, val totalPrice: Money)


internal data class ValidateReturnedRentalItemsCommand(@TargetAggregateIdentifier val rentalId: String)

