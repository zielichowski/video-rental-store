package com.javacaptain.video.rental.store.rental.config


import com.javacaptain.video.rental.store.rental.web.RentalDto

class RentalFixtures {
    static RentalDto rentalDto(
            String movieId = "1",
            Integer rentalPeriod = 5
    ) {
        new RentalDto(movieId, rentalPeriod)
    }
}
