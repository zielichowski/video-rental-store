package pl.zielichowski.rentalstore.rental.domain;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;
import pl.zielichowski.rentalstore.common.api.domain.Money;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.rental.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
class Rental {

    @AggregateIdentifier
    private String rentalId;
    private String userId;
    private LocalDate rentalDate;
    private RentalStatus rentalStatus;
    private List<MovieId> moviesToReturn = new ArrayList<>();
    private Money totalPrice;
    private Money totalSurcharge;
    private Integer bonusPoints;

    @AggregateMember
    private List<Movie> rentalEntries;

    Rental() {
    }

    @CommandHandler
    Rental(SubmitRentalCommand command) {
        log.info("Creating rental with command={} ", command.toString());
        apply(new RentalSubmittedEvent(
                command.getRentalId(),
                command.getInventoryId(),
                command.getUserId(),
                command.getRentalItems(),
                command.getDate()));
    }

    @EventSourcingHandler
    public void on(RentalSubmittedEvent event) {
        this.rentalId = event.getRentalId();
        this.userId = event.getUserId();
        this.rentalDate = event.getDate();
        this.rentalEntries = createMovies(event);
        this.totalPrice = calculateTotalPrice();
        this.bonusPoints = calculateBonusPoints();
        this.rentalStatus = RentalStatus.CREATED;
    }

    @CommandHandler
    public void handle(AcceptRentalCommand command) {
        log.info("CompleteRentalCommand  command={}", command.toString());
        if (rentalStatus == RentalStatus.CREATED) {
            apply(new RentalAcceptedEvent(
                    command.getRentalId(),
                    this.userId,
                    this.rentalDate,
                    convertToRentedMovies()));
            apply(new BonusPointsCalculatedEvent(this.rentalId, this.userId, this.bonusPoints));
        }
    }

    @EventSourcingHandler
    public void on(RentalAcceptedEvent event) {
        this.rentalStatus = RentalStatus.ACCEPTED;
        event
                .getMovies()
                .getRentedMovies()
                .forEach(rentedMovie -> moviesToReturn.add(rentedMovie.getMovieId()));
    }

    @CommandHandler
    public void handle(RejectRentalCommand command) {
        log.info("RejectRentalCommand command={}", command.toString());
        if (rentalStatus == RentalStatus.CREATED) {
            apply(new RentalRejectedEvent(command.getRentalId(), this.userId));
        }
    }

    @EventSourcingHandler
    public void on(RentalRejectedEvent event) {
        log.info("RentalRejectedEvent  event={}", event.toString());
        this.rentalStatus = RentalStatus.REJECTED;
    }

    @CommandHandler
    public void handle(ReturnRentalItemCommand command) {
        log.info("Return rental item command={}", command.toString());
        boolean match =
                moviesToReturn
                        .stream()
                        .anyMatch(movieId -> movieId.equals(command.getMovieId()));
        if (match) {
            apply(new RentalItemReturnedEvent(command.getRentalId(), command.getMovieId(), command.getReturnDate()));
        }
    }

    @EventSourcingHandler
    public void on(RentalItemReturnedEvent event) {
        this.moviesToReturn.remove(event.getMovieId());
    }

    @CommandHandler
    public void handle(ValidateReturnedRentalItemsCommand command) {
        if (this.moviesToReturn.isEmpty()) {
            apply(new RentalFinishedEvent(command.getRentalId(), this.totalSurcharge));
        }
    }

    @EventSourcingHandler
    public void on(RentalFinishedEvent event) {
        this.rentalStatus = RentalStatus.FINISHED;
    }


    @EventSourcingHandler
    public void on(SurchargeCalculatedEvent event) {
        if (this.totalSurcharge != null) {
            this.totalSurcharge = this.totalSurcharge.add(event.getSurcharge());
        } else {
            this.totalSurcharge = event.getSurcharge();
        }
    }

    private RentedMovies convertToRentedMovies() {
        List<RentedMovie> rentedMovies = rentalEntries.stream()
                .map(Movie::toRentedMovie)
                .collect(Collectors.toList());
        return new RentedMovies(rentedMovies, totalPrice);
    }


    private List<Movie> createMovies(RentalSubmittedEvent event) {
        return event.getRentalItems()
                .stream()
                .map(rentalItem ->
                        new Movie(
                                rentalItem.getMovieId(),
                                rentalItem.getMovieTypeName(),
                                rentalItem.getDaysOfRental(),
                                event.getDate()))
                .collect(Collectors.toList());
    }

    private Money calculateTotalPrice() {
        return this.rentalEntries
                .stream()
                .map(Movie::getPrice)
                .reduce(Money::add)
                .orElseThrow(() -> new TotalPriceCalculationException(rentalId));
    }

    private Integer calculateBonusPoints() {
        return this.rentalEntries.stream()
                .mapToInt(Movie::getBonusPoints)
                .sum();
    }

}
