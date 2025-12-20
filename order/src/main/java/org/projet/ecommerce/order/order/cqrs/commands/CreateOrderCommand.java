package org.projet.ecommerce.order.order.cqrs.commands;

import lombok.Builder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.projet.ecommerce.order.order.PaymentMethod;
import org.projet.ecommerce.order.product.PurchaseRequest;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CreateOrderCommand(
    @TargetAggregateIdentifier
    Integer id,
    String reference,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String customerId,
    List<PurchaseRequest> products
) {}
