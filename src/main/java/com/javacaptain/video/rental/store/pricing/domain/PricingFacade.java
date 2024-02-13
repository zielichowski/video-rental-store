package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;
import com.javacaptain.video.rental.store.pricing.api.PriceRequest;
import com.javacaptain.video.rental.store.pricing.api.SurchargeRequest;
import java.util.List;

public class PricingFacade {
  private final SurchargeCalculator surchargeCalculator;
  private final PricingMovieService pricingMovieService;

  PricingFacade(SurchargeCalculator surchargeCalculator, PricingMovieService pricingMovieService) {
    this.pricingMovieService = pricingMovieService;
    this.surchargeCalculator = surchargeCalculator;
  }

  public Money calculatePrice(List<PriceRequest> priceRequests) {
    return priceRequests.stream()
        .map(this::calculatePrice)
        .reduce(Money::add)
        .orElseThrow(IllegalArgumentException::new);
  }

  public Money calculatePrice(PriceRequest priceRequest) {
    final var movieType = pricingMovieService.getMovie(priceRequest.movieId()).movieType();
    final var pricing = PricingFactory.from(movieType);
    return pricing.price(priceRequest.rentalPeriod().daysOfRental());
  }

  public Money calculateSurcharge(List<SurchargeRequest> surchargeRequests) {
    return surchargeRequests.stream()
        .map(this::calculateSurcharge)
        .reduce(Money::add)
        .orElseThrow(IllegalArgumentException::new);
  }

  public Money calculateSurcharge(SurchargeRequest surchargeRequest) {
    return surchargeCalculator.calculateSurcharge(
        surchargeRequest.extraRentalDays(), surchargeRequest.movieId());
  }

  public void addMovie(MovieCreatedEvent movieCreatedEvent) {
    final var movie = new PricingMovie(movieCreatedEvent.movieId(), movieCreatedEvent.movieType());
    pricingMovieService.saveMovie(movie);
  }
}
