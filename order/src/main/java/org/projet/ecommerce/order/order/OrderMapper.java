package org.projet.ecommerce.order.order;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderMapper {


  public Order toOrder(OrderRequest request) {
    if (request == null) {
      return null;
    }
    return Order.builder()
        .id(request.id())
        .reference(request.reference() != null ? request.reference() : UUID.randomUUID().toString())
        .totalAmount(request.amount())
        .paymentMethod(request.paymentMethod())
        .customerId(request.customerId())
        .build();
  }

  public OrderResponse fromOrder(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getReference(),
        order.getTotalAmount(),
        order.getPaymentMethod(),
        order.getCustomerId()
    );
  }
}
