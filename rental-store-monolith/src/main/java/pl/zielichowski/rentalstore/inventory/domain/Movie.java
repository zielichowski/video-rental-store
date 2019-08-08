package pl.zielichowski.rentalstore.inventory.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.modelling.command.EntityId;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo;

import java.util.Objects;

@Slf4j
class Movie {
    @EntityId
    @Getter
    private MovieId movieId;
    private MovieInfo movieInfo;
    private MovieStatus movieStatus;

    Movie(MovieId movieId, MovieInfo movieInfo, MovieStatus movieStatus) {
        this.movieId = movieId;
        this.movieInfo = movieInfo;
        this.movieStatus = movieStatus;
    }

    boolean isAvailable() {
        return movieStatus == MovieStatus.AVAILABLE;
    }

    void rent() {
        if (movieStatus == MovieStatus.AVAILABLE) {
            log.info("Rent movie with id={}", movieId);
            movieStatus = MovieStatus.RENTED;
        }
    }

    void returnMovie() {
        this.movieStatus = MovieStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return movieId.equals(movie.movieId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId);
    }
}
