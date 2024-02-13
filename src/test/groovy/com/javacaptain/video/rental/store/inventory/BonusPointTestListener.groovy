package com.javacaptain.video.rental.store.inventory

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent
import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent

class BonusPointTestListener implements BonusPointEventListener {
    public List<MovieCreatedEvent> received = new ArrayList<>()

    @Override
    void handle(MovieCreatedEvent movieCreatedEvent) {
        received.add(movieCreatedEvent)
    }

    @Override
    void handle(RentalCreatedEvent rentalCreatedEvent) {

    }
}
