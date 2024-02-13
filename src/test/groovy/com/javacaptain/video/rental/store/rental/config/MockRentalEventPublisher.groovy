package com.javacaptain.video.rental.store.rental.config

import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent
import com.javacaptain.video.rental.store.rental.domain.RentalEvenPublisher

class MockRentalEventPublisher implements RentalEvenPublisher {

    @Override
    void publish(RentalCreatedEvent rentalCreatedEvent) {
        println "Published event=$rentalCreatedEvent"
    }
}
