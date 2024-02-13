package com.javacaptain.video.rental.store.rental.api;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.RentalPeriod;

public record RentalItemPriceRequest(MovieId movieId, RentalPeriod rentalPeriod) {}
