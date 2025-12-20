package org.projet.ecommerce.order.order.cqrs.events;

import lombok.Builder;
import org.projet.ecommerce.order.order.PaymentMethod;
import org.projet.ecommerce.order.product.PurchaseRequest;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderCreatedEvent(
    Integer id,
    String reference,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String customerId,
    List<PurchaseRequest> products
) {}
