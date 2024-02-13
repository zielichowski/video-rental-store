package com.javacaptain.video.rental.store.bonuspoints.infrastructure;

import com.javacaptain.video.rental.store.bonuspoints.api.BonusPointsRequest;
import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener;
import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointsFacade;
import com.javacaptain.video.rental.store.inventory.api.MovieCreatedEvent;
import com.javacaptain.video.rental.store.rental.api.RentalCreatedEvent;

public class InMemoryBonusPointEventListener implements BonusPointEventListener {
  private final BonusPointsFacade bonusPointsFacade;

  public InMemoryBonusPointEventListener(
      BonusPointsFacade bonusPointsFacade) {
    this.bonusPointsFacade = bonusPointsFacade;
  }

  @Override
  public void handle(MovieCreatedEvent movieCreatedEvent) {
    bonusPointsFacade.addMovie(movieCreatedEvent);
  }

  @Override
  public void handle(RentalCreatedEvent rentalCreatedEvent) {
    bonusPointsFacade.addPoints(
        new BonusPointsRequest(rentalCreatedEvent.clientId(), rentalCreatedEvent.rentedMovies()));
  }
}
