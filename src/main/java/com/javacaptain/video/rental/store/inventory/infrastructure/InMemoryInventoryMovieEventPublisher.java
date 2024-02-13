package com.javacaptain.video.rental.store.inventory.infrastructure;

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener;
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;
import com.javacaptain.video.rental.store.inventory.domain.InventoryMovieEventPublisher;
import com.javacaptain.video.rental.store.pricing.domain.PricingEventListener;

public class InMemoryInventoryMovieEventPublisher implements InventoryMovieEventPublisher {
  private final PricingEventListener pricingEventListener;
  private final BonusPointEventListener bonusPointEventListener;

  public InMemoryInventoryMovieEventPublisher(
          PricingEventListener pricingEventListener, BonusPointEventListener bonusPointEventListener) {
    this.pricingEventListener = pricingEventListener;
    this.bonusPointEventListener = bonusPointEventListener;
  }

  @Override
  public void publish(MovieCreatedEvent movieCreatedEvent) {
    pricingEventListener.handle(movieCreatedEvent);
    bonusPointEventListener.handle(movieCreatedEvent);
  }
}
