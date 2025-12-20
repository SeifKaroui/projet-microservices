package org.projet.ecommerce.product.product.cqrs.commands;

import lombok.Builder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Builder
public record CreateProductCommand(
    @TargetAggregateIdentifier
    Integer id,
    String name,
    String description,
    double availableQuantity,
    BigDecimal price,
    Integer categoryId
) {}
