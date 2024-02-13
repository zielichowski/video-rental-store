package com.javacaptain.video.rental.store.inventory.domain;

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener;
import com.javacaptain.video.rental.store.inventory.infrastructure.InMemoryInventoryMovieEventPublisher;
import com.javacaptain.video.rental.store.pricing.domain.PricingEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class InventoryMovieConfiguration {
  @Bean
  InventoryMovieFacade movieFacade(
      InventoryMovieRepository inventoryMovieRepository,
      InventoryMovieEventPublisher inventoryMovieEventPublisher) {
    return new InventoryMovieFacade(inventoryMovieRepository, inventoryMovieEventPublisher);
  }

  @Bean
  InventoryMovieEventPublisher inventoryMovieEventPublisher(
      PricingEventListener pricingEventListener, BonusPointEventListener bonusPointEventListener) {
    return new InMemoryInventoryMovieEventPublisher(pricingEventListener, bonusPointEventListener);
  }
}
