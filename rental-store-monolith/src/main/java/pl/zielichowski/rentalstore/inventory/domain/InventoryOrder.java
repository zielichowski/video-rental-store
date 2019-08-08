package pl.zielichowski.rentalstore.inventory.domain;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import pl.zielichowski.rentalstore.common.api.inventory.CreateInventoryOrderCommand;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderCreatedEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
class InventoryOrder {

    @AggregateIdentifier
    private String inventoryOrderId;
    private String inventoryId;
    private Status status;

    InventoryOrder() {
    }

    @CommandHandler
    public InventoryOrder(CreateInventoryOrderCommand command) {
        apply(new InventoryOrderCreatedEvent(command.getInventoryOrderId(), command.getInventoryId(), command.getMovies()));
    }

    @EventSourcingHandler
    public void on(InventoryOrderCreatedEvent event) {
        this.inventoryOrderId = event.getInventoryOrderId();
        this.inventoryId = event.getInventoryId();
        this.status = Status.NEW;
    }

    @EventSourcingHandler
    public void on(InventoryOrderValidatedWithSuccessEvent event) {
        this.status = Status.VALIDATED;
    }

    @EventSourcingHandler
    public void on(InventoryOrderValidatedWithErrorEvent event) {
        this.status = Status.ERROR;
    }

    private enum Status {
        NEW, VALIDATED, ERROR
    }
}
