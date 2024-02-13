package com.javacaptain.video.rental.store.inventory.web;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.inventory.api.MovieView;
import com.javacaptain.video.rental.store.inventory.domain.InventoryMovieFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static java.lang.StringTemplate.STR;

@RestController
class InventoryController {
    private final InventoryMovieFacade inventoryMovieFacade;

    InventoryController(InventoryMovieFacade inventoryMovieFacade) {
        this.inventoryMovieFacade = inventoryMovieFacade;
    }

    @PostMapping("/movies")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<URI> createMovie(@RequestBody CreateMovieRequest createInventoryRequest) {
        return ResponseEntity.created(URI.create(STR."/movies/\{inventoryMovieFacade.saveMovie(createInventoryRequest)}")).build();
    }

    @GetMapping(value = "/movies/{movieId}")
    public ResponseEntity<MovieView> getMovies(@PathVariable String movieId) {
        return ResponseEntity.ok(inventoryMovieFacade.getMovie(new MovieId(movieId)));
    }
}
