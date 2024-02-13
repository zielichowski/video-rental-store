package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.MovieId;

public interface PricingMovieService {
    void saveMovie(PricingMovie pricingMovieEntity);

    PricingMovie getMovie(MovieId movieId);
}
