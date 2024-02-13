package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;

public interface PricingEventListener {
    void handle(MovieCreatedEvent movieCreatedEvent);
}
