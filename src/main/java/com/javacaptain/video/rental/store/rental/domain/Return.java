package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.rental.api.RentalId;
import com.javacaptain.video.rental.store.rental.api.ReturnDate;
import com.javacaptain.video.rental.store.rental.api.ReturnId;

public record Return(ReturnId returnId, RentalId rentalId, ReturnDate returnDate, Money surcharge) {}
