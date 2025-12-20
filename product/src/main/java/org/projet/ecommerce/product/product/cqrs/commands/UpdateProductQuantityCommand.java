package org.projet.ecommerce.product.product.cqrs.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateProductQuantityCommand(
    @TargetAggregateIdentifier
    Integer id,
    double quantity
) {}
