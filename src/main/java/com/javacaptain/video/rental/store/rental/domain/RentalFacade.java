package com.javacaptain.video.rental.store.rental.domain;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.RentalPeriod;
import com.javacaptain.video.rental.store.rental.api.ClientId;
import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent;
import com.javacaptain.video.rental.store.rental.api.RentalDate;
import com.javacaptain.video.rental.store.rental.api.RentalId;
import com.javacaptain.video.rental.store.rental.api.RentalItemPriceRequest;
import com.javacaptain.video.rental.store.rental.api.RentalItemSurchargeRequest;
import com.javacaptain.video.rental.store.rental.api.RentalPriceRequest;
import com.javacaptain.video.rental.store.rental.api.RentalSurchargeRequest;
import com.javacaptain.video.rental.store.rental.api.ReturnDate;
import com.javacaptain.video.rental.store.rental.api.ReturnId;
import com.javacaptain.video.rental.store.rental.web.CreateRentalRequest;
import com.javacaptain.video.rental.store.rental.web.RentalDto;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Rental module looks like a one with potential to become more and more complex,
 * thus we should keep our domain and database model separately.
 * Furthermore, the Pricing module is treated as an external dependency.
 * The implementation is simplified, basically focusing only on the happy path.
 */
public class RentalFacade {
  private final PricingAdapter pricingAdapter;
  private final RentalRepository rentalRepository;
  private final ReturnRepository returnRepository;
  private final RentalEvenPublisher rentalEvenPublisher;
  private final Clock clock;

  RentalFacade(
      PricingAdapter pricingAdapter,
      RentalRepository rentalRepository,
      ReturnRepository returnRepository,
      RentalEvenPublisher rentalEvenPublisher,
      Clock clock) {
    this.pricingAdapter = pricingAdapter;
    this.rentalRepository = rentalRepository;
    this.returnRepository = returnRepository;
    this.rentalEvenPublisher = rentalEvenPublisher;
    this.clock = clock;
  }

  public Rental rent(CreateRentalRequest createRentalRequest) {
    final var rentalItemPriceRequest = mapToItemPriceRequest(createRentalRequest);
    final var price = pricingAdapter.calculatePrice(new RentalPriceRequest(rentalItemPriceRequest));
    final var rentalId = new RentalId(UUID.randomUUID().toString());
    final var rentalDate = new RentalDate(LocalDate.now(clock));
    final var rental =
        new Rental(
            rentalId,
            rentalDate,
            price,
            mapToRentalItems(createRentalRequest.rentalItems()),
            new ClientId(createRentalRequest.clientId()));
    rentalRepository.save(rental);
    rentalEvenPublisher.publish(RentalCreatedEvent.from(rental));
    return rental;
  }

  public Return returnRental(RentalId rentalId) {
    final var rental = rentalRepository.findRentalById(rentalId);
    final var returnDate = new ReturnDate(LocalDate.now(clock));
    final var surchargeRequests =
        rental.returnItems(returnDate).stream()
            .map(
                lateReturn ->
                    new RentalItemSurchargeRequest(
                        lateReturn.movieId(), lateReturn.extraRentalDays()))
            .toList();

    final var surcharge =
        pricingAdapter.calculateSurcharge(new RentalSurchargeRequest(surchargeRequests));
    final var rentalReturn =
        new Return(new ReturnId(UUID.randomUUID().toString()), rentalId, returnDate, surcharge);
    returnRepository.save(rentalReturn);
    return rentalReturn;
  }

  private List<RentalItem> mapToRentalItems(List<RentalDto> rentalDtos) {
    return rentalDtos.stream()
        .map(
            rentalDto ->
                new RentalItem(
                    new MovieId(rentalDto.movieId()), new RentalPeriod(rentalDto.daysOfRental())))
        .toList();
  }

  private List<RentalItemPriceRequest> mapToItemPriceRequest(
      CreateRentalRequest createRentalRequest) {
    return createRentalRequest.rentalItems().stream()
        .map(
            rentalItem ->
                new RentalItemPriceRequest(
                    new MovieId(rentalItem.movieId()), new RentalPeriod(rentalItem.daysOfRental())))
        .toList();
  }
}
