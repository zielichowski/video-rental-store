package com.javacaptain.video.rental.store.bonuspoints.domain;

import com.javacaptain.video.rental.store.common.MovieId;

public class BonusPointMovieService {
  private final JpaBonusPointMovieRepository jpaBonusPointMovieRepository;

  BonusPointMovieService(JpaBonusPointMovieRepository jpaBonusPointMovieRepository) {
    this.jpaBonusPointMovieRepository = jpaBonusPointMovieRepository;
  }

  public void saveMovie(BonusPointMovie bonusPointMovie) {
    jpaBonusPointMovieRepository.save(bonusPointMovie);
  }

  public BonusPointMovie getMovie(MovieId movieId) {
    return jpaBonusPointMovieRepository
        .findById(movieId.movieIdentifier())
        .orElseThrow(IllegalArgumentException::new);
  }
}
