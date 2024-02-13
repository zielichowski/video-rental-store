package com.javacaptain.video.rental.store.pricing.api;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.MovieType;

public record MovieDetails(MovieId movieId, MovieType movieType) {}
