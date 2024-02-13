 package com.javacaptain.video.rental.store.rental.web;

 import com.javacaptain.video.rental.store.rental.api.RentalId;
 import com.javacaptain.video.rental.store.rental.domain.RentalFacade;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RestController;

 import java.net.URI;

 import static java.lang.StringTemplate.STR;

 @RestController
 class RentalController {
    private final RentalFacade rentalFacade;

    RentalController(RentalFacade rentalFacade) {
        this.rentalFacade = rentalFacade;
    }

    @PostMapping("/rentals")
    public ResponseEntity<RentalResponse> createRental(@RequestBody CreateRentalRequest createRentalRequest){
        final var rental = rentalFacade.rent(createRentalRequest);
        return ResponseEntity
                .created(
                        URI.create(STR."/movies/\{rental.rentalId().value()}"))
                .body(new RentalResponse(rental.rentalId().value(),rental.totalPrice()));
    }

    @PostMapping("/rentals/{rentalId}/returns")
    public ResponseEntity<ReturnResponse> returnMovies(@PathVariable String rentalId){
        final var returnRental = rentalFacade.returnRental(new RentalId(rentalId));
        return ResponseEntity.ok(new ReturnResponse(returnRental.returnId().value(),returnRental.surcharge()));
    }
 }
