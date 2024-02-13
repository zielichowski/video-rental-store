package com.javacaptain.video.rental.store.rental.infrastructure;

import com.javacaptain.video.rental.store.common.Money;
import com.javacaptain.video.rental.store.rental.api.RentalId;
import com.javacaptain.video.rental.store.rental.api.ReturnDate;
import com.javacaptain.video.rental.store.rental.api.ReturnId;
import com.javacaptain.video.rental.store.rental.domain.Return;
import com.javacaptain.video.rental.store.rental.domain.ReturnRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class SqlReturnRepository implements ReturnRepository {
  private final JpaReturnRepository jpaReturnRepository;

  public SqlReturnRepository(JpaReturnRepository jpaReturnRepository) {
    this.jpaReturnRepository = jpaReturnRepository;
  }

  @Override
  public void save(Return rentalReturn) {
    final var returnEntity = new ReturnEntity();
    returnEntity.returnDate =
        rentalReturn.returnDate().value().atStartOfDay().toInstant(ZoneOffset.UTC);
    returnEntity.returnId = rentalReturn.returnId().value();
    returnEntity.surcharge_currency = rentalReturn.surcharge().currencyCode();
    returnEntity.surcharge_denomination = rentalReturn.surcharge().denomination();
    returnEntity.rentalId = rentalReturn.rentalId().value();
    jpaReturnRepository.save(returnEntity);
  }

  @Override
  public Return findById(ReturnId returnId) {
    return jpaReturnRepository
        .findById(returnId.value())
        .map(
            returnEntity ->
                new Return(
                    returnId,
                    new RentalId(returnEntity.rentalId),
                    new ReturnDate(
                        LocalDate.ofInstant(returnEntity.returnDate, ZoneOffset.UTC.normalized())),
                    new Money(
                        returnEntity.surcharge_denomination, returnEntity.surcharge_currency)))
        .orElseThrow(IllegalArgumentException::new);
  }
}
