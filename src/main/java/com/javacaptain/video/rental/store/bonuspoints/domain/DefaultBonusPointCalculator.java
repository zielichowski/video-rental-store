package com.javacaptain.video.rental.store.bonuspoints.domain;

import com.javacaptain.video.rental.store.bonuspoints.api.BonusPoint;
import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.MovieType;
import java.util.List;

class DefaultBonusPointCalculator implements BonusPointCalculator {
  private final BonusPointMovieService bonusPointMovieService;

  DefaultBonusPointCalculator(BonusPointMovieService bonusPointMovieService) {
    this.bonusPointMovieService = bonusPointMovieService;
  }

  @Override
  public BonusPoint calculate(List<MovieId> movies) {
    return movies.stream()
        .map(movieId -> MovieType.valueOf(bonusPointMovieService.getMovie(movieId).movieType))
        .map(BonusPointFactory::from)
        .reduce(BonusPoint::add)
        .orElseThrow(IllegalArgumentException::new);
  }
}
