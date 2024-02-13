package com.javacaptain.video.rental.store.inventory


import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent
import com.javacaptain.video.rental.store.pricing.domain.PricingEventListener

class PricingTestListener implements PricingEventListener {
    public List<MovieCreatedEvent> received = new ArrayList<>()

    @Override
    void handle(MovieCreatedEvent movieCreatedEvent) {
        received.add(movieCreatedEvent)
    }
}
