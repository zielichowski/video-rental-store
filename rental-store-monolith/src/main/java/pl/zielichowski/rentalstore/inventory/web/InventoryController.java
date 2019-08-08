package pl.zielichowski.rentalstore.inventory.web;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo;
import pl.zielichowski.rentalstore.common.api.inventory.CreateInventoryCommand;
import pl.zielichowski.rentalstore.inventory.query.InventoryView;
import pl.zielichowski.rentalstore.inventory.query.InventoryViewRepository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RepositoryRestController
class InventoryController {
    private final PagedResourcesAssembler<InventoryView> pagedAssembler;
    private final CommandGateway commandGateway;
    private final RepositoryEntityLinks entityLinks;
    private final InventoryViewRepository inventoryViewRepository;


    @PostMapping("/inventories")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createInventory(@RequestBody CreateInventoryRequest createInventoryRequest) {
        String inventoryId = UUID.randomUUID().toString();
        Map<MovieId, MovieInfo> movies = new HashMap<>();
        createInventoryRequest
                .getMovies()
                .forEach(movie -> movies.putIfAbsent(new MovieId(UUID.randomUUID().toString()), new MovieInfo(movie.getTitle(), movie.getType())));
        CreateInventoryCommand createInventoryCommand = new CreateInventoryCommand(inventoryId, movies);
        commandGateway.send(createInventoryCommand);
        return ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(InventoryViewRepository.class, inventoryId).getHref())).build();
    }

    @GetMapping(value = "/inventories")
    public ResponseEntity<Resources<Resource<InventoryView>>> getInventories(
            PersistentEntityResourceAssembler entityAssembler,
            Pageable pageable) {

        Page<InventoryView> inventoryViews = inventoryViewRepository.findAll(pageable);
        @SuppressWarnings({"unchecked", "rawtypes"})
        PagedResources<Resource<InventoryView>> resource = pagedAssembler.toResource(inventoryViews, (ResourceAssembler) entityAssembler);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
