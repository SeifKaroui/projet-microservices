package org.projet.ecommerce.order.order.cqrs.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.projet.ecommerce.order.order.PaymentMethod;
import org.projet.ecommerce.order.order.cqrs.commands.CreateOrderCommand;
import org.projet.ecommerce.order.order.cqrs.events.OrderCreatedEvent;

import java.math.BigDecimal;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private Integer id;
    private String reference;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String customerId;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        AggregateLifecycle.apply(OrderCreatedEvent.builder()
                .id(command.id())
                .reference(command.reference())
                .amount(command.amount())
                .paymentMethod(command.paymentMethod())
                .customerId(command.customerId())
                .products(command.products())
                .build());
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.id = event.id();
        this.reference = event.reference();
        this.amount = event.amount();
        this.paymentMethod = event.paymentMethod();
        this.customerId = event.customerId();
    }
}
