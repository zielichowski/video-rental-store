package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.Money;

interface Priceable {
  Money price(Integer daysOfRental);
}
