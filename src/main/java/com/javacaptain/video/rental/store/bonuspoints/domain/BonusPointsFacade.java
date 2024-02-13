package com.javacaptain.video.rental.store.bonuspoints.domain;

import com.javacaptain.video.rental.store.bonuspoints.api.BonusPoint;
import com.javacaptain.video.rental.store.bonuspoints.api.BonusPointsRequest;
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;
import com.javacaptain.video.rental.store.rental.api.ClientId;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class BonusPointsFacade {
  private final BonusPointsRepository bonusPointsRepository;
  private final BonusPointCalculator bonusPointCalculator;
  private final BonusPointMovieService bonusPointMovieService;

  BonusPointsFacade(
      BonusPointsRepository bonusPointsRepository,
      BonusPointCalculator bonusPointCalculator,
      BonusPointMovieService bonusPointMovieService) {
    this.bonusPointsRepository = bonusPointsRepository;
    this.bonusPointCalculator = bonusPointCalculator;
    this.bonusPointMovieService = bonusPointMovieService;
  }

  @Transactional
  public void addPoints(BonusPointsRequest points) {
    final var calculated = bonusPointCalculator.calculate(points.movies());

    final var bonusPoints =
        bonusPointsRepository
            .findByOwner(points.clientId().value())
            .orElseGet(
                () -> new BonusPoints(UUID.randomUUID().toString(), points.clientId().value(), 0));
    bonusPoints.number += calculated.value();
    bonusPointsRepository.save(bonusPoints);
  }

  public void addMovie(MovieCreatedEvent movieCreatedEvent) {
    final var movie = new BonusPointMovie();
    movie.movieId = movieCreatedEvent.movieId().movieIdentifier();
    movie.movieType = movieCreatedEvent.movieType().name();
    bonusPointMovieService.saveMovie(movie);
  }

  public BonusPoint getPoints(ClientId clientId){
    return bonusPointsRepository
        .findByOwner(clientId.value())
        .map(bonusPoints -> new BonusPoint(bonusPoints.number))
        .orElseThrow(IllegalArgumentException::new);
  }
}
