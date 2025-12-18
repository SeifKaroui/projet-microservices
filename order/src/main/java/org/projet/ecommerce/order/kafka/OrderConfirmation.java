package org.projet.ecommerce.order.kafka;


import org.projet.ecommerce.order.customer.CustomerResponse;
import org.projet.ecommerce.order.order.PaymentMethod;
import org.projet.ecommerce.order.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
