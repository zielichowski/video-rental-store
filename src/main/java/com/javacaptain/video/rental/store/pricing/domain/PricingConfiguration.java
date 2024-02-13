package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.pricing.infrastructure.InMemoryPricingEventListener;
import com.javacaptain.video.rental.store.pricing.infrastructure.InMemoryPricingMovieService;
import com.javacaptain.video.rental.store.pricing.infrastructure.JpaPricingMovieRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PricingConfiguration {
  @Bean
  PricingFacade pricingFacade(PricingMovieService pricingMovieService) {
    return new PricingFacade(
        new DefaultSurchargeCalculator(pricingMovieService), pricingMovieService);
  }

  @Bean
  PricingMovieService pricingMovieService(JpaPricingMovieRepository jpaPricingMovieRepository) {
    return new InMemoryPricingMovieService(jpaPricingMovieRepository);
  }

  @Bean
  PricingEventListener pricingEventListener(PricingFacade pricingFacade) {
    return new InMemoryPricingEventListener(pricingFacade);
  }
}
