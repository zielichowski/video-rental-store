package pl.zielichowski.rentalstore.inventory.query;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.zielichowski.rentalstore.common.api.domain.MovieInfo;
import pl.zielichowski.rentalstore.common.api.inventory.PublicMovieStatus;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Access(AccessType.FIELD)
@Getter
@EqualsAndHashCode
class MovieView {
    @Id
    private String movieId;
    @Embedded
    private MovieInfo movieInfo;
    private PublicMovieStatus movieStatus;

    MovieView(String movieId, MovieInfo movieInfo, PublicMovieStatus movieStatus) {
        this.movieId = movieId;
        this.movieInfo = movieInfo;
        this.movieStatus = movieStatus;
    }

    void changeStatus(PublicMovieStatus movieStatus) {
        this.movieStatus = movieStatus;
    }

}
