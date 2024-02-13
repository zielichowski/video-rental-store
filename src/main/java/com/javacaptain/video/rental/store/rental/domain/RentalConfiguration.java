package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener;
import com.javacaptain.video.rental.store.pricing.domain.PricingFacade;
import com.javacaptain.video.rental.store.rental.infrastructure.InMemoryPricingAdapter;
import com.javacaptain.video.rental.store.rental.infrastructure.InMemoryRentalEventPublisher;
import com.javacaptain.video.rental.store.rental.infrastructure.JpaRentalRepository;
import com.javacaptain.video.rental.store.rental.infrastructure.JpaReturnRepository;
import com.javacaptain.video.rental.store.rental.infrastructure.SqlRentalRepository;
import com.javacaptain.video.rental.store.rental.infrastructure.SqlReturnRepository;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RentalConfiguration {
  @Bean
  RentalFacade rentalFacade(
      PricingAdapter pricingAdapter,
      RentalRepository rentalRepository,
      ReturnRepository returnRepository,
      RentalEvenPublisher rentalEvenPublisher) {
    return new RentalFacade(
        pricingAdapter, rentalRepository, returnRepository, rentalEvenPublisher, Clock.systemUTC());
  }

  @Bean
  PricingAdapter pricingAdapter(PricingFacade pricingFacade) {
    return new InMemoryPricingAdapter(pricingFacade);
  }

  @Bean
  RentalRepository rentalRepository(JpaRentalRepository jpaRentalRepository) {
    return new SqlRentalRepository(jpaRentalRepository);
  }

  @Bean
  ReturnRepository returnRepository(JpaReturnRepository jpaReturnRepository) {
    return new SqlReturnRepository(jpaReturnRepository);
  }

  @Bean
  RentalEvenPublisher rentalEvenPublisher(BonusPointEventListener bonusPointEventListener) {
    return new InMemoryRentalEventPublisher(bonusPointEventListener);
  }
}
