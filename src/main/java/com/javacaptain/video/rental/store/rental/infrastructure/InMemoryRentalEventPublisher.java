package com.javacaptain.video.rental.store.rental.infrastructure;

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener;
import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent;
import com.javacaptain.video.rental.store.rental.domain.RentalEvenPublisher;

public class InMemoryRentalEventPublisher implements RentalEvenPublisher {
    private final BonusPointEventListener bonusPointEventListener;

    public InMemoryRentalEventPublisher(BonusPointEventListener bonusPointEventListener) {
        this.bonusPointEventListener = bonusPointEventListener;
    }

    @Override
    public void publish(RentalCreatedEvent rentalCreatedEvent) {
        bonusPointEventListener.handle(rentalCreatedEvent);
    }
}
