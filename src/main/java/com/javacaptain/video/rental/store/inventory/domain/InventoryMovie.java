package com.javacaptain.video.rental.store.inventory.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
class InventoryMovie {
  @Id String movieId;

  String movieType;
  String movieTitle;

  String movieDescription;

  public InventoryMovie(String movieId, String movieType, String movieTitle, String movieDescription) {
    this.movieId = movieId;
    this.movieType = movieType;
    this.movieTitle = movieTitle;
    this.movieDescription = movieDescription;
  }

  protected InventoryMovie() {}
}
