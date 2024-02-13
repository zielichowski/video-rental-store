package com.javacaptain.video.rental.store.rental.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "return")
class ReturnEntity {
    @Id
    String returnId;

    Instant returnDate;

    BigDecimal surcharge_denomination;

    String surcharge_currency;

    String rentalId;
}
