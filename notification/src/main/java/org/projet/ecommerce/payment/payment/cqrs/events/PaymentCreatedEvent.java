package org.projet.ecommerce.payment.payment.cqrs.events;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record PaymentCreatedEvent(
    Integer id,
    BigDecimal amount,
    Object paymentMethod, // Using Object to avoid dependency on PaymentMethod enum for now
    Integer orderId,
    String orderReference,
    String customerFirstname,
    String customerLastname,
    String customerEmail
) {}
