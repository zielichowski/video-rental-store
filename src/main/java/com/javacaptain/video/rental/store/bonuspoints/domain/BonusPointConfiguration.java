package com.javacaptain.video.rental.store.bonuspoints.domain;

import com.javacaptain.video.rental.store.bonuspoints.infrastructure.InMemoryBonusPointEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BonusPointConfiguration {
  @Bean
  BonusPointEventListener bonusPointEventListener(BonusPointsFacade bonusPointsFacade) {
    return new InMemoryBonusPointEventListener(bonusPointsFacade);
  }

  @Bean
  BonusPointsFacade bonusPointsFacade(
      BonusPointsRepository bonusPointsRepository, BonusPointMovieService bonusPointMovieService) {
    return new BonusPointsFacade(
        bonusPointsRepository,
        new DefaultBonusPointCalculator(bonusPointMovieService),
        bonusPointMovieService);
  }

  @Bean
  BonusPointMovieService bonusPointMovieService(
      JpaBonusPointMovieRepository jpaBonusPointMovieRepository) {
    return new BonusPointMovieService(jpaBonusPointMovieRepository);
  }
}
