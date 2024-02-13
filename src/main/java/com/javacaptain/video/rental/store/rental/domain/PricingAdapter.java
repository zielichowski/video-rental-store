package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.rental.api.RentalPriceRequest;
import com.javacaptain.video.rental.store.rental.api.RentalSurchargeRequest;

public interface PricingAdapter {
    Money calculatePrice(RentalPriceRequest rentalPriceRequest);

    Money calculateSurcharge(RentalSurchargeRequest rentalSurchargeRequest);
}
