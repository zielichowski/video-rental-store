package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.common.MovieType;
import java.math.BigDecimal;

class PricingFactory {
  private static final MoneyBasedPricing EXPENSIVE =
      new MoneyBasedPricing(
          new Money(new BigDecimal(40), "USD"), new PricingAlgorithm.FixedPricingAlgorithm());
  private static final MoneyBasedPricing REGULAR =
      new MoneyBasedPricing(
          new Money(new BigDecimal(30), "USD"),
          new PricingAlgorithm.ProgressivePricingAlgorithm(3));
  private static final MoneyBasedPricing BASIC =
      new MoneyBasedPricing(
          new Money(new BigDecimal(30), "USD"),
          new PricingAlgorithm.ProgressivePricingAlgorithm(5));

  /**
   * A simplification that leads to coupling. We should create our own movieType class within the
   * pricing domain.
   */
  public static MoneyBasedPricing from(MovieType movieType) {
    return switch (movieType) {
      case NEW_RELEASE -> EXPENSIVE;
      case REGULAR -> REGULAR;
      case OLD -> BASIC;
    };
  }
}
