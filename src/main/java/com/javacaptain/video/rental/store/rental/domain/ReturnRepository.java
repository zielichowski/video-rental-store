package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.rental.api.ReturnId;

public interface ReturnRepository {
  void save(Return rentalReturn);

  Return findById(ReturnId returnId);
}
