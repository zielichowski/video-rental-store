package com.javacaptain.video.rental.store.pricing.api;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.RentalPeriod;

public record PriceRequest(MovieId movieId, RentalPeriod rentalPeriod) {}
