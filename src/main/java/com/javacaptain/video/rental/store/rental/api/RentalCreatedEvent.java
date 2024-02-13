package com.javacaptain.video.rental.store.rental.api;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.rental.domain.Rental;
import com.javacaptain.video.rental.store.rental.domain.RentalItem;
import java.util.List;

public record RentalCreatedEvent(
    RentalId rentalId,
    RentalDate rentalDate,
    Money totalPrice,
    ClientId clientId,
    List<MovieId> rentedMovies) {

    public static RentalCreatedEvent from(Rental rental){
        return new RentalCreatedEvent(
                rental.rentalId(),
                rental.rentalDate(),
                rental.totalPrice(),
                rental.clientId(),
                mapToRentedMovies(rental.rentalItems()));
    }

    private static List<MovieId> mapToRentedMovies(List<RentalItem> rentalItems) {
        return rentalItems.stream().map(r -> new MovieId(r.movieId().movieIdentifier())).toList();
    }
}
