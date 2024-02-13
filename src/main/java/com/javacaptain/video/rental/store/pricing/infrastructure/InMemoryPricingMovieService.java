package com.javacaptain.video.rental.store.pricing.infrastructure;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.MovieType;
import com.javacaptain.video.rental.store.pricing.domain.PricingMovie;
import com.javacaptain.video.rental.store.pricing.domain.PricingMovieService;

public class InMemoryPricingMovieService implements PricingMovieService {
  private final JpaPricingMovieRepository jpaPricingMovieRepository;

  public InMemoryPricingMovieService(JpaPricingMovieRepository jpaPricingMovieRepository) {
    this.jpaPricingMovieRepository = jpaPricingMovieRepository;
  }

  @Override
  public void saveMovie(PricingMovie pricingMovie) {
    final var pricingMovieEntity =
        new PricingMovieEntity(
            pricingMovie.movieId().movieIdentifier(), pricingMovie.movieType().name());
    jpaPricingMovieRepository.save(pricingMovieEntity);
  }

  @Override
  public PricingMovie getMovie(MovieId movieId) {
    return jpaPricingMovieRepository
        .findById(movieId.movieIdentifier())
        .map(me -> new PricingMovie(new MovieId(me.movieId), MovieType.valueOf(me.movieType)))
        .orElseThrow(IllegalArgumentException::new);
  }
}
