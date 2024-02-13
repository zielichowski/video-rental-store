package com.javacaptain.video.rental.store.rental.config

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent
import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent

class BonusPointTestListener implements BonusPointEventListener {
    public List<RentalCreatedEvent> received = new ArrayList<>()

    @Override
    void handle(MovieCreatedEvent movieCreatedEvent) {
    }

    @Override
    void handle(RentalCreatedEvent rentalCreatedEvent) {
        received.add(rentalCreatedEvent)
    }
}
