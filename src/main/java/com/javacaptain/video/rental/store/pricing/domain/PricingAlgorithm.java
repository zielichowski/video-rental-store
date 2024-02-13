package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.Money;

sealed interface PricingAlgorithm {
  Money calculatePrice(Integer daysOfRental, Money basePrice);

  record ProgressivePricingAlgorithm(Integer threshold) implements PricingAlgorithm {
    @Override
    public Money calculatePrice(Integer daysOfRental, Money basePrice) {
      if (daysOfRental > threshold) {
        var multiplier = daysOfRental - threshold;
        var money = basePrice.multiplyBy(multiplier);
        return basePrice.add(money);
      } else {
        return basePrice;
      }
    }
  }

  record FixedPricingAlgorithm() implements PricingAlgorithm {
    @Override
    public Money calculatePrice(Integer daysOfRental, Money basePrice) {
      return basePrice.multiplyBy(daysOfRental);
    }
  }
}
