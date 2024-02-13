package com.javacaptain.video.rental.store.bonuspoints.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bonus_point_movie")
public class BonusPointMovie {
    public BonusPointMovie() {
    }

    public BonusPointMovie(String movieId, String movieType) {
        this.movieId = movieId;
        this.movieType = movieType;
    }

    @Id
    String movieId;
    String movieType;
}
