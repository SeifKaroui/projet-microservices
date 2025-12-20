package org.projet.ecommerce.payment.payment.cqrs.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.projet.ecommerce.payment.payment.cqrs.commands.CreatePaymentCommand;
import org.projet.ecommerce.payment.payment.cqrs.events.PaymentCreatedEvent;

@Aggregate
public class PaymentAggregate {

    @AggregateIdentifier
    private Integer id;

    public PaymentAggregate() {
    }

    @CommandHandler
    public PaymentAggregate(CreatePaymentCommand command) {
        AggregateLifecycle.apply(PaymentCreatedEvent.builder()
                .id(command.id())
                .amount(command.amount())
                .paymentMethod(command.paymentMethod())
                .orderId(command.orderId())
                .orderReference(command.orderReference())
                .customerFirstname(command.customerFirstname())
                .customerLastname(command.customerLastname())
                .customerEmail(command.customerEmail())
                .build());
    }

    @EventSourcingHandler
    public void on(PaymentCreatedEvent event) {
        this.id = event.id();
    }
}
