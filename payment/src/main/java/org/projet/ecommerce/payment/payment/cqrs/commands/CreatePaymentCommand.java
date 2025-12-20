package org.projet.ecommerce.payment.payment.cqrs.commands;

import lombok.Builder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.projet.ecommerce.payment.payment.PaymentMethod;

import java.math.BigDecimal;

@Builder
public record CreatePaymentCommand(
    @TargetAggregateIdentifier
    Integer id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    String customerFirstname,
    String customerLastname,
    String customerEmail
) {}
