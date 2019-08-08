package pl.zielichowski.rentalstore.rental.domain;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import pl.zielichowski.rentalstore.common.api.domain.MovieId;
import pl.zielichowski.rentalstore.common.api.inventory.CreateInventoryOrderCommand;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent;
import pl.zielichowski.rentalstore.common.api.inventory.ReturnMovieCommand;
import pl.zielichowski.rentalstore.common.api.rental.AcceptRentalCommand;
import pl.zielichowski.rentalstore.common.api.rental.CalculatePossibleSurchargesCommand;
import pl.zielichowski.rentalstore.common.api.rental.RejectRentalCommand;
import pl.zielichowski.rentalstore.common.api.rental.RentalAcceptedEvent;
import pl.zielichowski.rentalstore.common.api.rental.RentalFinishedEvent;
import pl.zielichowski.rentalstore.common.api.rental.RentalItem;
import pl.zielichowski.rentalstore.common.api.rental.RentalItemReturnedEvent;
import pl.zielichowski.rentalstore.common.api.rental.RentalSubmittedEvent;
import pl.zielichowski.rentalstore.common.api.rental.SurchargeCalculatedEvent;

import java.util.List;
import java.util.stream.Collectors;

@Saga
@Slf4j
public class RentalSaga {
    private transient CommandGateway commandGateway;
    private String rentalId;
    private String inventoryId;
    private List<MovieId> movieIds;


    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }


    public RentalSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "rentalId")
    public void on(RentalSubmittedEvent event) {
        log.info("Rental submitted event={}", event.toString());
        this.rentalId = event.getRentalId();
        this.inventoryId = event.getInventoryId();

        this.movieIds = event.getRentalItems()
                .stream()
                .map(RentalItem::getMovieId)
                .collect(Collectors.toList());

        String inventoryOrderId = "inventoryOrder_" + event.getRentalId();
        SagaLifecycle.associateWith("inventoryOrderId", inventoryOrderId);
        commandGateway.send(new CreateInventoryOrderCommand(inventoryOrderId, event.getInventoryId(), movieIds));
    }

    @SagaEventHandler(associationProperty = "inventoryOrderId")
    public void on(InventoryOrderValidatedWithSuccessEvent event) {
        log.info("InventoryOrderValidatedWithSuccessEvent is saga event={}", event.toString());
        commandGateway.send(new AcceptRentalCommand(this.rentalId));
    }

    @SagaEventHandler(associationProperty = "rentalId")
    public void on(RentalAcceptedEvent event) {
        log.info("Rental marked as completed in saga. Event={} ", event.toString());
    }

    @SagaEventHandler(associationProperty = "inventoryOrderId")
    @EndSaga
    public void on(InventoryOrderValidatedWithErrorEvent event) {
        log.info("InventoryOrderValidatedWithErrorEvent is saga event={}", event.toString());
        commandGateway.send(new RejectRentalCommand(this.rentalId));
    }

    @SagaEventHandler(associationProperty = "rentalId")
    public void on(RentalItemReturnedEvent event) {
        log.info("RentalItemReturnedEvent event={}", event.toString());
        commandGateway.send(new ReturnMovieCommand(inventoryId, event.getMovieId()));
        commandGateway.send(new CalculatePossibleSurchargesCommand(event.getRentalId(), event.getMovieId(), event.getReturnDate()));
    }

    @SagaEventHandler(associationProperty = "rentalId")
    public void on(SurchargeCalculatedEvent event) {
        log.info("SurchargeCalculatedEvent event={}", event.toString());
        commandGateway.send(new ValidateReturnedRentalItemsCommand(event.getRentalId()));
    }

    @SagaEventHandler(associationProperty = "rentalId")
    @EndSaga
    public void on(RentalFinishedEvent event) {
        log.info("RentalFinishedEvent event={}", event.toString());
    }

}
