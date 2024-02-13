package com.javacaptain.video.rental.store.rental.infrastructure;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.common.RentalPeriod;
import com.javacaptain.video.rental.store.rental.api.ClientId;
import com.javacaptain.video.rental.store.rental.api.RentalDate;
import com.javacaptain.video.rental.store.rental.api.RentalId;
import com.javacaptain.video.rental.store.rental.domain.Rental;
import com.javacaptain.video.rental.store.rental.domain.RentalItem;
import com.javacaptain.video.rental.store.rental.domain.RentalRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlRentalRepository implements RentalRepository {
  private final JpaRentalRepository jpaRentalRepository;

  public SqlRentalRepository(JpaRentalRepository jpaRentalRepository) {
    this.jpaRentalRepository = jpaRentalRepository;
  }

  @Override
  public Rental findRentalById(RentalId rentalId) {
    return jpaRentalRepository
        .findById(rentalId.value())
        .map(
            rentalEntity ->
                new Rental(
                    rentalId,
                    new RentalDate(
                        LocalDate.ofInstant(rentalEntity.rentalDate, ZoneOffset.UTC.normalized())),
                    new Money(rentalEntity.denomination, rentalEntity.currency),
                    mapRentalItems(rentalEntity.rentalItemEntities),
                    new ClientId(rentalEntity.clientId)))
        .orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public void save(Rental rental) {
    final var rentalEntity = new RentalEntity();
    rentalEntity.rentalId = rental.rentalId().value();
    rentalEntity.rentalDate = rental.rentalDate().value().atStartOfDay().toInstant(ZoneOffset.UTC);
    rentalEntity.denomination = rental.totalPrice().denomination();
    rentalEntity.currency = rental.totalPrice().currencyCode();
    rentalEntity.clientId = rental.clientId().value();
    rentalEntity.rentalItemEntities = mapToRentalItemEntities(rental, rentalEntity);
    jpaRentalRepository.save(rentalEntity);
  }

  private Set<RentalItemEntity> mapToRentalItemEntities(Rental rental, RentalEntity rentalEntity) {
    return rental.rentalItems().stream()
        .map(
            rentalItem ->
                new RentalItemEntity(
                    new RentalItemId(rentalItem.movieId().movieIdentifier(), rentalEntity.rentalId),
                    rentalEntity,
                    rentalItem.rentalPeriod().daysOfRental()))
        .collect(Collectors.toSet());
  }

  private List<RentalItem> mapRentalItems(Set<RentalItemEntity> rentalItemEntities) {
    return rentalItemEntities.stream()
        .map(
            rentalItemEntity ->
                new RentalItem(
                    new MovieId(rentalItemEntity.id.movieId),
                    new RentalPeriod(rentalItemEntity.rentalDays)))
        .toList();
  }
}
