package pl.zielichowski.rentalstore.rental.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.EntityId;
import pl.zielichowski.rentalstore.common.api.domain.Money;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.domain.MovieTypeName;
import pl.zielichowski.rentalstore.common.api.rental.CalculatePossibleSurchargesCommand;
import pl.zielichowski.rentalstore.common.api.rental.RentedMovie;
import pl.zielichowski.rentalstore.common.api.rental.SurchargeCalculatedEvent;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@EqualsAndHashCode
class Movie {
    @EntityId
    @Getter
    private MovieId movieId;
    @Getter
    private Money price;
    private Money surcharge;
    private int daysOfRental;
    private LocalDate rentalDay;
    private MovieType movieType;
    @Getter
    private Integer bonusPoints;

    Movie(MovieId movieId, MovieTypeName movieTypeName, int daysOfRental, LocalDate rentalDay) {
        this.movieId = movieId;
        this.daysOfRental = daysOfRental;
        this.rentalDay = rentalDay;
        this.movieType = MovieType.Companion.of(movieTypeName);
        this.price = movieType.calculatePrice(daysOfRental);
        this.bonusPoints = movieType.calculateBonusPoints();
    }

    RentedMovie toRentedMovie() {
        return new RentedMovie(this.movieId, this.daysOfRental, this.price);
    }

    @CommandHandler
    public void on(CalculatePossibleSurchargesCommand command) {
        long daysOfDelay = 0;
        if (command.getReturnDate().isAfter(rentalDay.plusDays(daysOfRental))) {
            daysOfDelay = calculateDelayDays(command);
        }
        Money surcharge = movieType.calculateSurcharge(Math.toIntExact(daysOfDelay));
        apply(new SurchargeCalculatedEvent(command.getRentalId(), command.getMovieId(), surcharge));
    }

    @EventSourcingHandler
    public void handle(SurchargeCalculatedEvent event) {
        this.surcharge = event.getSurcharge();
    }

    private long calculateDelayDays(CalculatePossibleSurchargesCommand command) {
        return ChronoUnit.DAYS.between(rentalDay.plusDays(daysOfRental), command.getReturnDate());
    }
}
