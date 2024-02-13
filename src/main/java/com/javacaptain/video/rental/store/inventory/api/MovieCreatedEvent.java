package com.javacaptain.video.rental.store.inventory.api;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.MovieType;

public record MovieCreatedEvent(
        MovieId movieId,
        MovieType movieType,
        String movieTitle, // Should be strong type
        String movieDescription

) {}
