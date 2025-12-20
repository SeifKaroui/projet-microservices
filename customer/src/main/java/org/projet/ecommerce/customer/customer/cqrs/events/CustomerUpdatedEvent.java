package org.projet.ecommerce.customer.customer.cqrs.events;

import lombok.Builder;
import org.projet.ecommerce.customer.customer.Address;

@Builder
public record CustomerUpdatedEvent(
    String id,
    String firstname,
    String lastname,
    String email,
    Address address
) {}
