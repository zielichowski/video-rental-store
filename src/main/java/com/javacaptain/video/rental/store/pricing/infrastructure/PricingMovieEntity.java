package com.javacaptain.video.rental.store.pricing.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pricing_movie")
public class PricingMovieEntity {
    @Id
    public String movieId;

    public PricingMovieEntity(String movieId, String movieType) {
        this.movieId = movieId;
        this.movieType = movieType;
    }

    public PricingMovieEntity() {
    }

    public String movieType;
}
