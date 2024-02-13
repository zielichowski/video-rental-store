package com.javacaptain.video.rental.store.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal denomination, String currencyCode) {

  public static final String DEFAULT_CURRENCY = "USD";
  public static final Money ZERO = new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);

  public Money(Double denomination, String currencyCode) {
    this(new BigDecimal(denomination).setScale(2, RoundingMode.UP), currencyCode);
  }

  public Money multiplyBy(Integer multiplier) {
    return multiplyBy(new BigDecimal(multiplier));
  }

  public Money multiplyBy(BigDecimal multiplier) {
    return new Money(denomination.multiply(multiplier).setScale(2, RoundingMode.UP), currencyCode);
  }

  public Money add(Money money) {
    checkCurrencyCompatibility(money);
    return new Money(
        denomination.add(money.denomination).setScale(2, RoundingMode.UP), currencyCode);
  }

  private void checkCurrencyCompatibility(Money money) {
    if (incompatibleCurrency(money)) {
      throw new IllegalArgumentException(
              STR."Currency mismatch : \{currencyCode} -> \{money.currencyCode}");
    }
  }

  private Boolean incompatibleCurrency(Money money) {
    return !Objects.equals(currencyCode, money.currencyCode);
  }
}
