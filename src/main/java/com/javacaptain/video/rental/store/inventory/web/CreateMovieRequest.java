package com.javacaptain.video.rental.store.inventory.web;

import com.javacaptain.video.rental.store.common.MovieType;

public record CreateMovieRequest(String movieTitle, MovieType movieType, String movieDesc) {}
