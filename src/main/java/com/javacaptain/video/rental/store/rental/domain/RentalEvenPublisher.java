package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent;

public interface RentalEvenPublisher {
  void publish(RentalCreatedEvent rentalCreatedEvent);
}
