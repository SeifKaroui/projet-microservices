package org.projet.ecommerce.customer.customer.cqrs.commands;

import lombok.Builder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.projet.ecommerce.customer.customer.Address;

@Builder
public record UpdateCustomerCommand(
    @TargetAggregateIdentifier
    String id,
    String firstname,
    String lastname,
    String email,
    Address address
) {}
