package com.javacaptain.video.rental.store.rental.infrastructure;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "rental")
public class RentalEntity {
  @Id
  String rentalId;

  Instant rentalDate;

  BigDecimal denomination;

  String currency;

  String clientId;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "rentalEntity", cascade = CascadeType.ALL)
  Set<RentalItemEntity> rentalItemEntities = new LinkedHashSet<>();
}
