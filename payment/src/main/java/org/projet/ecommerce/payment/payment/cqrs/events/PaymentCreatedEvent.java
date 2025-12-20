package org.projet.ecommerce.payment.payment.cqrs.events;

import lombok.Builder;
import org.projet.ecommerce.payment.payment.PaymentMethod;

import java.math.BigDecimal;

@Builder
public record PaymentCreatedEvent(
    Integer id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    String customerFirstname,
    String customerLastname,
    String customerEmail
) {}
