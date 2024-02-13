package com.javacaptain.video.rental.store.rental.config

import com.javacaptain.video.rental.store.common.Money
import com.javacaptain.video.rental.store.rental.api.RentalPriceRequest
import com.javacaptain.video.rental.store.rental.api.RentalSurchargeRequest
import com.javacaptain.video.rental.store.rental.domain.PricingAdapter

class MockPricingAdapter implements PricingAdapter {
    @Override
    Money calculatePrice(RentalPriceRequest rentalPriceRequest) {
        return new Money(rentalPriceRequest.priceRequests().size() * 10, "USD")
    }

    @Override
    Money calculateSurcharge(RentalSurchargeRequest rentalSurchargeRequest) {
        return new Money(rentalSurchargeRequest.surchargeRequests().size() * 5, "USD")
    }
}
