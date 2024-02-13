package com.javacaptain.video.rental.store.rental.infrastructure;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.pricing.domain.PricingFacade;
import com.javacaptain.video.rental.store.rental.api.RentalPriceRequest;
import com.javacaptain.video.rental.store.rental.api.RentalSurchargeRequest;
import com.javacaptain.video.rental.store.rental.domain.PricingAdapter;

/**
 * At the moment it is on the simplest possible implementation,
 * however, in the future there may be many others using http communication or certain message brokers.
 */
public class InMemoryPricingAdapter implements PricingAdapter {
  private final PricingFacade pricingFacade;

  public InMemoryPricingAdapter(PricingFacade pricingFacade) {
    this.pricingFacade = pricingFacade;
  }

  @Override
  public Money calculatePrice(RentalPriceRequest rentalPriceRequest) {
    return pricingFacade.calculatePrice(rentalPriceRequest.toPriceRequest());
  }

  @Override
  public Money calculateSurcharge(RentalSurchargeRequest rentalSurchargeRequest) {
    return pricingFacade.calculateSurcharge(rentalSurchargeRequest.toSurchargeRequest());
  }
}
