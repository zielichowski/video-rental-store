package com.javacaptain.video.rental.store.rental.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.Hibernate;

@Embeddable
public class RentalItemId implements Serializable {
  public RentalItemId(String movieId, String rentalId) {
    this.movieId = movieId;
    this.rentalId = rentalId;
  }

  @NotNull
  @Column(name = "movie_id", nullable = false, length = Integer.MAX_VALUE)
  String movieId;

  @NotNull
  @Column(name = "rental_id", nullable = false, length = Integer.MAX_VALUE)
  String rentalId;

  public RentalItemId() {

  }

  public void setMovieId(String movieId) {
    this.movieId = movieId;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    RentalItemId entity = (RentalItemId) o;
    return Objects.equals(this.movieId, entity.movieId)
        && Objects.equals(this.rentalId, entity.rentalId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(movieId, rentalId);
  }
}
