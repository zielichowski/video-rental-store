package com.javacaptain.video.rental.store.bonuspoints.domain;

import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;
import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent;

public interface BonusPointEventListener {
    void handle(MovieCreatedEvent movieCreatedEvent);
    void handle(RentalCreatedEvent rentalCreatedEvent);
}
