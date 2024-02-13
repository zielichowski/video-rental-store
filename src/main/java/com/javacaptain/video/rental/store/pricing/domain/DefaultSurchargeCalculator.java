package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.ExtraRentalDays;
import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.common.MovieId;

final class DefaultSurchargeCalculator implements SurchargeCalculator {

  private final PricingMovieService pricingMovieService;

  DefaultSurchargeCalculator(PricingMovieService pricingMovieService) {
    this.pricingMovieService = pricingMovieService;
  }

  @Override
  public Money calculateSurcharge(ExtraRentalDays extraRentalDays, MovieId movieId) {
    return PricingFactory.from(pricingMovieService.getMovie(movieId).movieType())
        .basePrice()
        .multiplyBy(extraRentalDays.value());
  }
}
