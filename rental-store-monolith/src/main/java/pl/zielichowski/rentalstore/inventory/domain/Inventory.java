package pl.zielichowski.rentalstore.inventory.domain;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.inventory.CreateInventoryCommand;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryCreatedEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent;
import pl.zielichowski.rentalstore.common.api.inventory.MovieNotFoundException;
import pl.zielichowski.rentalstore.common.api.inventory.MovieReturnedEvent;
import pl.zielichowski.rentalstore.common.api.inventory.ReturnMovieCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
class Inventory {

    @AggregateIdentifier
    private String inventoryId;

    @AggregateMember
    private Map<MovieId, Movie> movies = new HashMap<>();

    protected Inventory() {
    }

    @CommandHandler
    public Inventory(CreateInventoryCommand command) {
        log.info("Creating inventory command={}", command.getInventoryId());
        apply(new InventoryCreatedEvent(command.getInventoryId(), command.getMovies()));
    }

    @EventSourcingHandler
    public void on(InventoryCreatedEvent event) {
        log.info("Creating inventory event={}", event.getInventoryId());
        this.inventoryId = event.getInventoryId();
        event.getMovies()
                .forEach((key, value) -> movies.putIfAbsent(key, new Movie(key, value, MovieStatus.AVAILABLE)));
    }

    @CommandHandler
    public void handle(ValidateInventoryOrderCommand command) {
        boolean allMatch = command
                .getMovies()
                .stream()
                .allMatch(movieId -> movies.get(movieId).isAvailable());
        if (allMatch) {
            apply(new InventoryOrderValidatedWithSuccessEvent(command.getInventoryId(), command.getInventoryOrderId(), command.getMovies()));
        } else {
            apply(new InventoryOrderValidatedWithErrorEvent(command.getInventoryId(), command.getInventoryOrderId()));
        }
    }

    @CommandHandler
    public void handle(ReturnMovieCommand command) {
        Movie movie =
                Optional.ofNullable(movies.get(command.getMovieId()))
                        .orElseThrow(() -> new MovieNotFoundException(command.getMovieId().getIdentifier()));
        if (!movie.isAvailable()) {
            apply(new MovieReturnedEvent(command.getInventoryId(), command.getMovieId()));
        } else {
            throw new IllegalArgumentException("Cannot return movie which is not rented! MovieId=" + command.getMovieId());
        }

    }

    @EventSourcingHandler
    public void on(MovieReturnedEvent event) {
        log.info("Movie returned event={} ", event.toString());
        movies.get(event.getMovieId())
                .returnMovie();
    }

    @EventSourcingHandler
    public void on(InventoryOrderValidatedWithSuccessEvent event) {
        log.info("InventoryOrderValidatedWithSuccessEvent event={}", event.toString());
        event.getMovies()
                .forEach(movieId -> movies.get(movieId).rent());
    }

    @EventSourcingHandler
    public void on(InventoryOrderValidatedWithErrorEvent event) {
        log.info("InventoryOrderValidatedWithErrorEvent event={}", event.toString());
    }
}
