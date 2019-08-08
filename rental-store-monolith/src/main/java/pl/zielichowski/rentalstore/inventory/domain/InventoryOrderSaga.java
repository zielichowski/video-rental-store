package pl.zielichowski.rentalstore.inventory.domain;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderCreatedEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithErrorEvent;
import pl.zielichowski.rentalstore.common.api.inventory.InventoryOrderValidatedWithSuccessEvent;

@Saga
@Slf4j
public class InventoryOrderSaga {

    private transient CommandGateway commandGateway;

    public InventoryOrderSaga() {
    }

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @SagaEventHandler(associationProperty = "inventoryOrderId")
    @StartSaga
    public void on(InventoryOrderCreatedEvent event) {
        commandGateway.send(new ValidateInventoryOrderCommand(event.getInventoryId(), event.getInventoryOrderId(), event.getMovies()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "inventoryOrderId")
    public void on(InventoryOrderValidatedWithSuccessEvent event) {
        log.info("InventoryOrderValidatedWithSuccessEvent in saga {event={}}", event.toString());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "inventoryOrderId")
    public void on(InventoryOrderValidatedWithErrorEvent event) {
        log.info("InventoryOrderValidatedWithErrorEvent in saga {event={}}", event.toString());
    }
}
