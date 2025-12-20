package org.projet.ecommerce.product.product.cqrs.events;

public record ProductQuantityUpdatedEvent(
    Integer id,
    double quantity
) {}
