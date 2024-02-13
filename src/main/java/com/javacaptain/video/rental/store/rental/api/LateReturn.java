package com.javacaptain.video.rental.store.rental.api;

import com.javacaptain.video.rental.store.common.ExtraRentalDays;
import com.javacaptain.video.rental.store.common.MovieId;

public record LateReturn(MovieId movieId, ExtraRentalDays extraRentalDays) {}
