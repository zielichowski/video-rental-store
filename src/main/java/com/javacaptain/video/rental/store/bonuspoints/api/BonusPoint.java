package com.javacaptain.video.rental.store.bonuspoints.api;

public record BonusPoint(Integer value) {
  public BonusPoint add(BonusPoint point) {
    return new BonusPoint(this.value + point.value());
  }
}
