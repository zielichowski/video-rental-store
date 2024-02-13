package com.javacaptain.video.rental.store.inventory.domain;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;
import com.javacaptain.video.rental.store.inventory.api.MovieView;
import com.javacaptain.video.rental.store.inventory.web.CreateMovieRequest;

import java.util.UUID;

/**
 * This is simple crud, so let's make it CRUD. We use the same database and domain model.
 */
public class InventoryMovieFacade {
    private final InventoryMovieRepository inventoryMovieRepository;
    private final InventoryMovieEventPublisher inventoryMovieEventPublisher;

    public InventoryMovieFacade(InventoryMovieRepository inventoryMovieRepository, InventoryMovieEventPublisher inventoryMovieEventPublisher) {
        this.inventoryMovieRepository = inventoryMovieRepository;
        this.inventoryMovieEventPublisher = inventoryMovieEventPublisher;
    }

    public String saveMovie(CreateMovieRequest createMovieRequest) {
        final var movieId = UUID.randomUUID().toString();
        final var inventoryMovie = new InventoryMovie(
                movieId,
                createMovieRequest.movieType().name(),
                createMovieRequest.movieTitle(),
                createMovieRequest.movieDesc());
        inventoryMovieRepository.save(inventoryMovie);
        inventoryMovieEventPublisher.publish(
                new MovieCreatedEvent(
                        new MovieId(movieId),
                        createMovieRequest.movieType(),
                        createMovieRequest.movieTitle(),
                        createMovieRequest.movieDesc()));
        return movieId;
    }

    public MovieView getMovie(MovieId movieId) {
        return inventoryMovieRepository
                .findById(movieId.movieIdentifier())
                .map(m -> new MovieView(m.movieId,m.movieType,m.movieTitle))
                .orElseThrow(() -> new IllegalArgumentException(STR."Unable to find bonusPointMovie with movieId=\{movieId}"));
    }
}
