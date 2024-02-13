package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.RentalPeriod;

public record RentalItem(MovieId movieId, RentalPeriod rentalPeriod) {}
