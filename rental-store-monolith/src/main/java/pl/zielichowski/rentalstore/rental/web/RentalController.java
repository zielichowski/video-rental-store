package pl.zielichowski.rentalstore.rental.web;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.inventory.MovieData;
import pl.zielichowski.rentalstore.common.api.rental.RentalItem;
import pl.zielichowski.rentalstore.common.api.rental.ReturnRentalItemCommand;
import pl.zielichowski.rentalstore.common.api.rental.SubmitRentalCommand;
import pl.zielichowski.rentalstore.inventory.query.FindMoviesDataInInventory;
import pl.zielichowski.rentalstore.rental.query.RentalView;
import pl.zielichowski.rentalstore.rental.query.RentalViewRepository;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@RepositoryRestController
@RequiredArgsConstructor
class RentalController {
    private final PagedResourcesAssembler<RentalView> pagedAssembler;
    private final RentalViewRepository rentalViewRepository;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final RepositoryEntityLinks entityLinks;

    @GetMapping(value = "/rentals")
    public ResponseEntity<Resources<Resource<RentalView>>> getRentals(
            PersistentEntityResourceAssembler entityAssembler,
            Pageable pageable, @RequestHeader("Api-key") String apiKey) {

        Page<RentalView> rentalView = rentalViewRepository.findAllByUserId(apiKey, pageable);
        @SuppressWarnings({"unchecked", "rawtypes"})
        PagedResources<Resource<RentalView>> resource = pagedAssembler.toResource(rentalView, (ResourceAssembler) entityAssembler);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/rentals/{rentalId}")
    public ResponseEntity<PersistentEntityResource> getRentalItem(
            PersistentEntityResourceAssembler entityAssembler,
            @RequestHeader("Api-key") String apiKey,
            @PathVariable("rentalId") String rentalId) {
        RentalView rentalView = rentalViewRepository.findByUserIdAndRentalId(apiKey, rentalId);
        PersistentEntityResource resource = entityAssembler.toFullResource(rentalView);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PostMapping(value = "/rentals")
    public ResponseEntity<Object> createRental(@RequestBody @Valid CreateRentalRequest createRentalRequest,
                                               @RequestHeader("Api-key") String apiKey) {
        String rentalId = UUID.randomUUID().toString();
        queryMovieData(createRentalRequest.inventoryId, createRentalRequest.rentalItemsRequest)
                .thenApply(movieData -> sendCommandToCreateRental(rentalId, createRentalRequest, apiKey, movieData));
        return ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(RentalViewRepository.class, rentalId).getHref())).build();

    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/rentals/{rentalId}/returns")
    public void returnMovie(
            @PathVariable String rentalId,
            @Valid @RequestBody ReturnItemsRequest returnRequest) {
        returnRequest
                .listOfItems
                .forEach(item ->
                        commandGateway.send(new ReturnRentalItemCommand(rentalId, new MovieId(item.movieId), LocalDate.now())));

    }

    private CompletableFuture<String> sendCommandToCreateRental(String rentalId,
                                                                CreateRentalRequest createRentalRequest,
                                                                String apiKey,
                                                                List<MovieData> movieData) {
        return commandGateway.send(new SubmitRentalCommand(
                rentalId,
                createRentalRequest.inventoryId,
                apiKey,
                createRentalItems(movieData, createRentalRequest.rentalItemsRequest.listOfItems),
                LocalDate.now()));
    }

    private List<RentalItem> createRentalItems(List<MovieData> movieData, List<RentalItemRequest> listOfItems) {
        return movieData
                .stream()
                .flatMap(md ->
                        listOfItems
                                .stream()
                                .filter(item -> md.getMovieId().getIdentifier().equals(item.movieId))
                                .map(item -> new RentalItem(md.getMovieId(), md.getMovieType(), item.getDaysOfRental())))
                .collect(toList());

    }

    private CompletableFuture<List<MovieData>> queryMovieData(String inventoryId, RentalItemsRequest rentalItemsRequest) {
        return queryGateway.query(
                new FindMoviesDataInInventory(
                        inventoryId,
                        rentalItemsRequest.listOfItems.stream().map(RentalItemRequest::getMovieId).collect(toList())),
                ResponseTypes.multipleInstancesOf(MovieData.class));
    }
}
