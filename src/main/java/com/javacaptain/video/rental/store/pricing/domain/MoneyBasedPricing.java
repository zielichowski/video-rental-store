package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.Money;

record MoneyBasedPricing(Money basePrice, PricingAlgorithm pricingAlgorithm) implements Priceable {
  @Override
  public Money price(Integer daysOfRental) {
    return pricingAlgorithm.calculatePrice(daysOfRental, basePrice);
  }
}
