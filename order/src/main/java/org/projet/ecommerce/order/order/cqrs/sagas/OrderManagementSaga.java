package org.projet.ecommerce.order.order.cqrs.sagas;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.spring.stereotype.Saga;
import org.projet.ecommerce.order.order.cqrs.events.OrderCreatedEvent;
import org.projet.ecommerce.order.product.PurchaseRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
public class OrderManagementSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent received for orderId: {}", event.id());

        // 1. Update Product Quantities
        for (PurchaseRequest product : event.products()) {
            // We use the same command class name/package as in product service
            // In a real app, these would be in a shared library
            commandGateway.send(new org.projet.ecommerce.product.product.cqrs.commands.UpdateProductQuantityCommand(
                    product.productId(),
                    product.quantity()
            ));
        }

        // 2. Create Payment
        // We need customer info which we don't have in the event yet, 
        // but for the sake of the demo, we'll assume we have it or use placeholders
        commandGateway.send(org.projet.ecommerce.payment.payment.cqrs.commands.CreatePaymentCommand.builder()
                .id(new java.util.Random().nextInt(1000000))
                .amount(event.amount())
                .paymentMethod(org.projet.ecommerce.payment.payment.PaymentMethod.valueOf(event.paymentMethod().name()))
                .orderId(event.id())
                .orderReference(event.reference())
                .customerFirstname("Customer") // Placeholder
                .customerLastname("Name") // Placeholder
                .customerEmail("customer@example.com") // Placeholder
                .build());
    }

    // In a real Saga, we would listen for PaymentCreatedEvent or ProductUpdatedEvent to continue or end the saga
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(org.projet.ecommerce.payment.payment.cqrs.events.PaymentCreatedEvent event) {
        log.info("PaymentCreatedEvent received for orderId: {}. Ending Saga.", event.orderId());
    }
}
