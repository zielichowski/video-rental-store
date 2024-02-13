package com.javacaptain.video.rental.store.pricing.domain;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.MovieType;

public record PricingMovie(MovieId movieId, MovieType movieType) {}
