package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.common.ExtraRentalDays;
import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.rental.api.ClientId;
import com.javacaptain.video.rental.store.rental.api.LateReturn;
import com.javacaptain.video.rental.store.rental.api.RentalDate;
import com.javacaptain.video.rental.store.rental.api.RentalId;
import com.javacaptain.video.rental.store.rental.api.ReturnDate;
import java.time.Period;
import java.util.List;

/**
 * Difficult dilemma-whether a class should be public or all classes within a domain should have
 * package-private access. Each solution has its pros and cons. However, in the end I decided that
 * it is better to separate the infrastructure package, which has a dependency to the Rental class.
 */
public record Rental(
    RentalId rentalId,
    RentalDate rentalDate,
    Money totalPrice,
    List<RentalItem> rentalItems,
    ClientId clientId) {
  List<LateReturn> returnItems(ReturnDate returnDate) {
    final var actualRentalDays = Period.between(rentalDate.value(), returnDate.value()).getDays();
    return rentalItems.stream()
        .filter(rentalItem -> rentalItem.rentalPeriod().daysOfRental() < actualRentalDays)
        .map(
            rentalItem ->
                new LateReturn(
                    rentalItem.movieId(),
                    new ExtraRentalDays(
                        actualRentalDays - rentalItem.rentalPeriod().daysOfRental())))
        .toList();
  }
}
