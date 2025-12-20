package org.projet.ecommerce.customer.customer.cqrs.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.projet.ecommerce.customer.customer.Address;
import org.projet.ecommerce.customer.customer.cqrs.commands.CreateCustomerCommand;
import org.projet.ecommerce.customer.customer.cqrs.commands.UpdateCustomerCommand;
import org.projet.ecommerce.customer.customer.cqrs.events.CustomerCreatedEvent;
import org.projet.ecommerce.customer.customer.cqrs.events.CustomerUpdatedEvent;

@Aggregate
public class CustomerAggregate {

    @AggregateIdentifier
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private Address address;

    public CustomerAggregate() {
    }

    @CommandHandler
    public CustomerAggregate(CreateCustomerCommand command) {
        AggregateLifecycle.apply(CustomerCreatedEvent.builder()
                .id(command.id())
                .firstname(command.firstname())
                .lastname(command.lastname())
                .email(command.email())
                .address(command.address())
                .build());
    }

    @CommandHandler
    public void handle(UpdateCustomerCommand command) {
        AggregateLifecycle.apply(CustomerUpdatedEvent.builder()
                .id(command.id())
                .firstname(command.firstname())
                .lastname(command.lastname())
                .email(command.email())
                .address(command.address())
                .build());
    }

    @EventSourcingHandler
    public void on(CustomerCreatedEvent event) {
        this.id = event.id();
        this.firstname = event.firstname();
        this.lastname = event.lastname();
        this.email = event.email();
        this.address = event.address();
    }

    @EventSourcingHandler
    public void on(CustomerUpdatedEvent event) {
        this.firstname = event.firstname();
        this.lastname = event.lastname();
        this.email = event.email();
        this.address = event.address();
    }
}
