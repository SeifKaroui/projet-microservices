package org.projet.ecommerce.product.product.cqrs.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.projet.ecommerce.product.product.cqrs.commands.CreateProductCommand;
import org.projet.ecommerce.product.product.cqrs.commands.UpdateProductQuantityCommand;
import org.projet.ecommerce.product.product.cqrs.events.ProductCreatedEvent;
import org.projet.ecommerce.product.product.cqrs.events.ProductQuantityUpdatedEvent;

import java.math.BigDecimal;

@Aggregate
public class ProductAggregate {

    @AggregateIdentifier
    private Integer id;
    private String name;
    private String description;
    private double availableQuantity;
    private BigDecimal price;
    private Integer categoryId;

    public ProductAggregate() {
        // Required by Axon
    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        AggregateLifecycle.apply(ProductCreatedEvent.builder()
                .id(command.id())
                .name(command.name())
                .description(command.description())
                .availableQuantity(command.availableQuantity())
                .price(command.price())
                .categoryId(command.categoryId())
                .build());
    }

    @CommandHandler
    public void handle(UpdateProductQuantityCommand command) {
        if (this.availableQuantity < command.quantity()) {
            throw new RuntimeException("Insufficient stock");
        }
        AggregateLifecycle.apply(new ProductQuantityUpdatedEvent(command.id(), command.quantity()));
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.id = event.id();
        this.name = event.name();
        this.description = event.description();
        this.availableQuantity = event.availableQuantity();
        this.price = event.price();
        this.categoryId = event.categoryId();
    }

    @EventSourcingHandler
    public void on(ProductQuantityUpdatedEvent event) {
        this.availableQuantity -= event.quantity();
    }
}
