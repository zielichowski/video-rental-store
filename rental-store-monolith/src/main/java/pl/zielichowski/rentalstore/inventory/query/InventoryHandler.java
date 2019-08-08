package pl.zielichowski.rentalstore.inventory.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryCreatedEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent;
import pl.zielichowski.rentalstore.common.api.inventory.MovieData;
import pl.zielichowski.rentalstore.common.api.inventory.MovieNotFoundException;
import pl.zielichowski.rentalstore.common.api.inventory.MovieReturnedEvent;
import pl.zielichowski.rentalstore.common.api.inventory.PublicMovieStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class InventoryHandler {

    private final InventoryViewRepository inventoryViewRepository;

    private final MovieViewRepository movieViewRepository;

    @EventHandler
    public void on(InventoryCreatedEvent event) {
        log.info("Movie created event in projection event={}", event.toString());

        Set<MovieView> movieEntities = event
                .getMovies()
                .entrySet()
                .stream()
                .map(entry -> new MovieView(entry.getKey().getIdentifier(), entry.getValue(), PublicMovieStatus.AVAILABLE))
                .collect(Collectors.toSet());

        InventoryView inventoryView = new InventoryView(event.getInventoryId(), movieEntities);
        inventoryViewRepository.save(inventoryView);
    }

    @EventHandler
    public void on(InventoryOrderValidatedWithSuccessEvent event) {
        event.getMovies()
                .forEach(movieId ->
                        movieViewRepository.findById(movieId.getIdentifier())
                                .ifPresent(movieView -> movieView.changeStatus(PublicMovieStatus.RENTED)));
    }

    @EventHandler
    public void on(MovieReturnedEvent event) {
        movieViewRepository.findById(
                event.getMovieId().getIdentifier())
                .ifPresent(
                        movieView -> movieView.changeStatus(PublicMovieStatus.AVAILABLE));
    }

    @QueryHandler
    public List<MovieData> on(FindMoviesDataInInventory query) {
        return query
                .getMoviesIds()
                .stream()
                .map(id -> {
                    MovieTypeName movieTypeName = movieViewRepository.findById(id)
                            .map(movieView -> movieView.getMovieInfo().type)
                            .orElseThrow(() -> new MovieNotFoundException(id));
                    return new MovieData(new MovieId(id), movieTypeName);

                })
                .collect(Collectors.toList());


    }
}
