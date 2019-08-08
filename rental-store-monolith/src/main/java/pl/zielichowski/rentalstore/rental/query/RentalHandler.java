package pl.zielichowski.rentalstore.rental.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import pl.zielichowski.rentalstore.common.api.rental.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
class RentalHandler {

    private final RentalViewRepository rentalViewRepository;

    private final RentalItemViewRepository rentalItemViewRepository;

    @EventHandler
    public void on(RentalAcceptedEvent event) {
        log.info("Rental completed in projection event={}", event.toString());
        RentalView rentalView = RentalView.builder()
                .rentalId(event.getRentalId())
                .userId(event.getUserId())
                .rentalItemViews(convert(event.getMovies().getRentedMovies(), event.getUserId()))
                .rentalStatus(PublicRentalStatus.ACCEPTED)
                .totalPrice(event.getMovies().getTotalPrice().toString())
                .build();
        rentalViewRepository.save(rentalView);
    }

    @EventHandler
    public void on(RentalItemReturnedEvent event) {
        log.info("Rental item returned in projection event={}", event.toString());
        rentalViewRepository
                .findById(event.getRentalId())
                .flatMap(rentalView ->
                        rentalView
                                .getRentalItemViews()
                                .stream()
                                .filter(item -> item.getItemId().equals(event.getMovieId().getIdentifier()))
                                .findFirst())
                .ifPresent(rentalItemView -> rentalItemView.setRentalItemStatus(RentalItemStatus.RETURNED));
    }

    @EventHandler
    public void on(RentalRejectedEvent event) {
        log.info("Rental rejected in projection event={}", event.toString());
        RentalView rentalView = RentalView.builder()
                .rentalId(event.getRentalId())
                .userId(event.getUserId())
                .rentalStatus(PublicRentalStatus.REJECTED)
                .build();

        rentalViewRepository
                .save(rentalView);
    }

    @EventHandler
    public void on(RentalFinishedEvent event) {
        log.info("Rental finished in projection event={}", event.toString());
        rentalViewRepository
                .findById(event.getRentalId())
                .ifPresent(rentalView -> {
                    rentalView.setRentalStatus(PublicRentalStatus.FINISHED);
                    rentalView.setTotalSurcharge(event.getTotalSurcharge().toString());
                });
    }

    @EventHandler
    public void on(SurchargeCalculatedEvent event) {
        log.info("SurchargeCalculatedEvent in projection event={}", event.toString());
        rentalViewRepository
                .findById(event.getRentalId())
                .flatMap(rentalView ->
                        rentalView
                                .getRentalItemViews()
                                .stream()
                                .filter(item -> item.getItemId().equals(event.getMovieId().getIdentifier()))
                                .findFirst())
                .ifPresent(rentalItemView -> rentalItemView.setSurcharge(event.getSurcharge().toString()));
    }

    private List<RentalItemView> convert(List<RentedMovie> rentedMovies, String userId) {
        return rentedMovies
                .stream()
                .map(rentalItem ->
                        RentalItemView.builder()
                                .daysOfRental(rentalItem.getDaysOfRental())
                                .userId(userId)
                                .itemId(rentalItem.getMovieId().getIdentifier())
                                .price(rentalItem.getPrice().toString())
                                .rentalItemStatus(RentalItemStatus.RENTED)
                                .build())
                .collect(Collectors.toList());
    }
}
