package com.javacaptain.video.rental.store.inventory.domain;

import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;

public interface InventoryMovieEventPublisher {
    void publish(MovieCreatedEvent movieCreatedEvent);
}
