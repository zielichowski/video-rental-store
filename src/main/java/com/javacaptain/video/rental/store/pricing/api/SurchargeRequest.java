package com.javacaptain.video.rental.store.pricing.api;

import com.javacaptain.video.rental.store.common.ExtraRentalDays;
import com.javacaptain.video.rental.store.common.MovieId;

public record SurchargeRequest(MovieId movieId, ExtraRentalDays extraRentalDays) {}
