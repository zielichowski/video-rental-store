package pl.zielichowski.rentalstore.rental.domain;

class TotalPriceCalculationException extends RuntimeException {

    TotalPriceCalculationException(String rentalId) {
        super(String.format("Cannot calculate total price for rental {rentalId=%s}", rentalId));
    }
}
