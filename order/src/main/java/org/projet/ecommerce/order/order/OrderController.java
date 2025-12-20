package org.projet.ecommerce.order.order;

import java.util.Collections;
import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService service;
  private final org.axonframework.commandhandling.gateway.CommandGateway commandGateway;
  private final org.axonframework.queryhandling.QueryGateway queryGateway;

  @PostMapping
  public ResponseEntity<Integer> createOrder(
      @RequestBody @Valid OrderRequest request
  ) {
    // return ResponseEntity.ok(this.service.createOrder(request));
    Integer id = new java.util.Random().nextInt(1000000);
    commandGateway.sendAndWait(org.projet.ecommerce.order.order.cqrs.commands.CreateOrderCommand.builder()
            .id(id)
            .reference(request.reference())
            .amount(request.amount())
            .paymentMethod(request.paymentMethod())
            .customerId(request.customerId())
            .products(request.products())
            .build());
    return ResponseEntity.ok(id);
  }

  @GetMapping
  @Retry(name = "myRetry", fallbackMethod = "fallback")
  @RateLimiter(name = "myRateLimiter", fallbackMethod = "fallback")
  @CircuitBreaker(name = "ordermicroService", fallbackMethod = "fallback")
  public ResponseEntity<List<OrderResponse>> findAll() {
    // return ResponseEntity.ok(this.service.findAllOrders());
    return ResponseEntity.ok(queryGateway.query(
            new org.projet.ecommerce.order.order.cqrs.projections.GetAllOrdersQuery(),
            org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf(OrderResponse.class)
    ).join());
  }

  @GetMapping("/{order-id}")
  public ResponseEntity<OrderResponse> findById(
      @PathVariable("order-id") Integer orderId
  ) {
    // return ResponseEntity.ok(this.service.findById(orderId));
    return ResponseEntity.ok(queryGateway.query(
            new org.projet.ecommerce.order.order.cqrs.projections.GetOrderByIdQuery(orderId),
            org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf(OrderResponse.class)
    ).join());
  }

  public ResponseEntity<List<OrderResponse>> fallback(Exception e) {
    // Log the exception for debugging
    System.err.println("Fallback triggered: " + e.getMessage());
    // Return an empty list as fallback
    return ResponseEntity.ok(Collections.emptyList());
  }
}
