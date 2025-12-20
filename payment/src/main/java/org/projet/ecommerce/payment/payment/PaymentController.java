package org.projet.ecommerce.payment.payment;

import java.util.Collections;
import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService service;
  private final org.axonframework.commandhandling.gateway.CommandGateway commandGateway;

  @PostMapping
  public ResponseEntity<Integer> createPayment(
      @RequestBody @Valid PaymentRequest request
  ) {
    // return ResponseEntity.ok(this.service.createPayment(request));
    Integer id = new java.util.Random().nextInt(1000000);
    commandGateway.sendAndWait(org.projet.ecommerce.payment.payment.cqrs.commands.CreatePaymentCommand.builder()
            .id(id)
            .amount(request.amount())
            .paymentMethod(request.paymentMethod())
            .orderId(request.orderId())
            .orderReference(request.orderReference())
            .customerFirstname(request.customer().firstname())
            .customerLastname(request.customer().lastname())
            .customerEmail(request.customer().email())
            .build());
    return ResponseEntity.ok(id);
  }

  @GetMapping
  @Retry(name = "myRetry", fallbackMethod = "fallback")
  @RateLimiter(name = "myRateLimiter", fallbackMethod = "fallback")
  @CircuitBreaker(name = "paymentmicroService", fallbackMethod = "fallback")
  public ResponseEntity<List<Payment>> findAll() {
    return ResponseEntity.ok(this.service.findAllPayments());
  }

  public ResponseEntity<List<Payment>> fallback(Exception e) {
    // Log the exception for debugging
    System.err.println("Fallback triggered: " + e.getMessage());
    // Return an empty list as fallback
    return ResponseEntity.ok(Collections.emptyList());
  }
}
