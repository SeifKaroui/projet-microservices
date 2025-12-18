package org.projet.ecommerce.order.payment;


import org.projet.ecommerce.order.customer.CustomerResponse;
import org.projet.ecommerce.order.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    CustomerResponse customer
) {
}
