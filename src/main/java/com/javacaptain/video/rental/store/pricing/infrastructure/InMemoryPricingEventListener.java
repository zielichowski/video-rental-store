package com.javacaptain.video.rental.store.pricing.infrastructure;

import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;
import com.javacaptain.video.rental.store.pricing.domain.PricingEventListener;
import com.javacaptain.video.rental.store.pricing.domain.PricingFacade;

public class InMemoryPricingEventListener implements PricingEventListener {
    private final PricingFacade pricingFacade;

    public InMemoryPricingEventListener(PricingFacade pricingFacade) {
        this.pricingFacade = pricingFacade;
    }

    @Override
    public void handle(MovieCreatedEvent movieCreatedEvent) {
        pricingFacade.addMovie(movieCreatedEvent);
    }
}
