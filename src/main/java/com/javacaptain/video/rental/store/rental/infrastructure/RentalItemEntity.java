package com.javacaptain.video.rental.store.rental.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "rental_item")
public class RentalItemEntity {
  public RentalItemEntity(RentalItemId id, RentalEntity rentalEntity, Integer rentalDays) {
    this.id = id;
    this.rentalEntity = rentalEntity;
    this.rentalDays = rentalDays;
  }

  @EmbeddedId RentalItemId id;

  @MapsId("rentalId")
  @ManyToOne
  @JoinColumn(name = "rental_id", nullable = false)
  RentalEntity rentalEntity;

  @NotNull
  @Column(name = "rental_days", nullable = false)
  Integer rentalDays;

  public RentalItemEntity() {

  }
}
