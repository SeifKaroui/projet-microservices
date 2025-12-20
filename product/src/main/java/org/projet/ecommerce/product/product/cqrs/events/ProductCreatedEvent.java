package org.projet.ecommerce.product.product.cqrs.events;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductCreatedEvent(
    Integer id,
    String name,
    String description,
    double availableQuantity,
    BigDecimal price,
    Integer categoryId
) {}
