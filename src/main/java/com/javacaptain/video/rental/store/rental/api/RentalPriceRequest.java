package com.javacaptain.video.rental.store.rental.api;

import com.javacaptain.video.rental.store.pricing.api.PriceRequest;
import java.util.List;

public record RentalPriceRequest(List<RentalItemPriceRequest> priceRequests) {
  public List<PriceRequest> toPriceRequest() {
    return this.priceRequests.stream()
        .map(pr -> new PriceRequest(pr.movieId(), pr.rentalPeriod()))
        .toList();
  }
}
