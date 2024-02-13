package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.rental.api.RentalId;

public interface RentalRepository {
  Rental findRentalById(RentalId rentalId);

  void save(Rental rental);
}
