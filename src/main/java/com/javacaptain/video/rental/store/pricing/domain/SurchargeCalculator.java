package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.ExtraRentalDays;
import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.common.MovieId;

interface SurchargeCalculator {
  Money calculateSurcharge(ExtraRentalDays extraRentalDays, MovieId movieId);
}
