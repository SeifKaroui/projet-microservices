package org.projet.ecommerce.notification.notification;

import java.util.Collections;
import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService service;

  @GetMapping
  @Retry(name = "myRetry", fallbackMethod = "fallback")
  @RateLimiter(name = "myRateLimiter", fallbackMethod = "fallback")
  @CircuitBreaker(name = "notificationmicroService", fallbackMethod = "fallback")
  public ResponseEntity<List<Notification>> findAll() {
    return ResponseEntity.ok(service.findAllNotifications());
  }

  public ResponseEntity<List<Notification>> fallback(Exception e) {
    // Log the exception for debugging
    System.err.println("Fallback triggered: " + e.getMessage());
    // Return an empty list as fallback
    return ResponseEntity.ok(Collections.emptyList());
  }
}

