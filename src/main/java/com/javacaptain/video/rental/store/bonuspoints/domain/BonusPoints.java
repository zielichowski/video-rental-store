package com.javacaptain.video.rental.store.bonuspoints.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bonus_point")
public class BonusPoints {
  @Id String bonusPointsId;
  String owner;
  Integer number;

  public BonusPoints(String bonusPointsId, String owner, Integer number) {
    this.bonusPointsId = bonusPointsId;
    this.owner = owner;
    this.number = number;
  }

  public BonusPoints() {
  }
}
