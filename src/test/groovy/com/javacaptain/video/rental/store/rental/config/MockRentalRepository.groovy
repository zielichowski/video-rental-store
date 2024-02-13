package com.javacaptain.video.rental.store.rental.config

import com.javacaptain.video.rental.store.rental.api.RentalId
import com.javacaptain.video.rental.store.rental.domain.Rental
import com.javacaptain.video.rental.store.rental.domain.RentalRepository

import java.util.concurrent.ConcurrentHashMap

class MockRentalRepository implements RentalRepository {
    def db = new ConcurrentHashMap<RentalId, Rental>()

    @Override
    Rental findRentalById(RentalId rentalId) {
        return db.get(rentalId)
    }

    @Override
    void save(Rental rental) {
        db[rental.rentalId()] = rental
    }
}
