package org.projet.ecommerce.customer.customer;

import java.util.Collections;
import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService service;
  private final org.axonframework.commandhandling.gateway.CommandGateway commandGateway;
  private final org.axonframework.queryhandling.QueryGateway queryGateway;

  @PostMapping
  public ResponseEntity<String> createCustomer(
      @RequestBody @Valid CustomerRequest request
  ) {
    // return ResponseEntity.ok(this.service.createCustomer(request));
    String id = java.util.UUID.randomUUID().toString();
    commandGateway.sendAndWait(org.projet.ecommerce.customer.customer.cqrs.commands.CreateCustomerCommand.builder()
            .id(id)
            .firstname(request.firstname())
            .lastname(request.lastname())
            .email(request.email())
            .address(request.address())
            .build());
    return ResponseEntity.ok(id);
  }

  @PutMapping
  public ResponseEntity<Void> updateCustomer(
      @RequestBody @Valid CustomerRequest request
  ) {
    // this.service.updateCustomer(request);
    commandGateway.sendAndWait(org.projet.ecommerce.customer.customer.cqrs.commands.UpdateCustomerCommand.builder()
            .id(request.id())
            .firstname(request.firstname())
            .lastname(request.lastname())
            .email(request.email())
            .address(request.address())
            .build());
    return ResponseEntity.accepted().build();
  }

  @GetMapping
  @Retry(name = "myRetry", fallbackMethod = "fallback")
  @RateLimiter(name = "myRateLimiter", fallbackMethod = "fallback")
  @CircuitBreaker(name = "customermicroService", fallbackMethod = "fallback")
  public ResponseEntity<List<CustomerResponse>> findAll() {
    // return ResponseEntity.ok(this.service.findAllCustomers());
    return ResponseEntity.ok(queryGateway.query(
            new org.projet.ecommerce.customer.customer.cqrs.projections.GetAllCustomersQuery(),
            org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf(CustomerResponse.class)
    ).join());
  }

  @GetMapping("/exists/{customer-id}")
  public ResponseEntity<Boolean> existsById(
      @PathVariable("customer-id") String customerId
  ) {
    return ResponseEntity.ok(this.service.existsById(customerId));
  }

  @GetMapping("/{customer-id}")
  public ResponseEntity<CustomerResponse> findById(
      @PathVariable("customer-id") String customerId
  ) {
    // return ResponseEntity.ok(this.service.findById(customerId));
    return ResponseEntity.ok(queryGateway.query(
            new org.projet.ecommerce.customer.customer.cqrs.projections.GetCustomerByIdQuery(customerId),
            org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf(CustomerResponse.class)
    ).join());
  }

  @DeleteMapping("/{customer-id}")
  public ResponseEntity<Void> delete(
      @PathVariable("customer-id") String customerId
  ) {
    this.service.deleteCustomer(customerId);
    return ResponseEntity.accepted().build();
  }

  public ResponseEntity<List<CustomerResponse>> fallback(Exception e) {
    // Log the exception for debugging
    System.err.println("Fallback triggered: " + e.getMessage());
    // Return an empty list as fallback
    return ResponseEntity.ok(Collections.emptyList());
  }

}
