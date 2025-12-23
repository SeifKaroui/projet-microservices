package org.projet.ecommerce.product.product;

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

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;
    private final org.axonframework.commandhandling.gateway.CommandGateway commandGateway;
    private final org.axonframework.queryhandling.QueryGateway queryGateway;


    @PostMapping
    public ResponseEntity<Integer> createProduct(
            @RequestBody @Valid ProductRequest request
    ) {
        // return ResponseEntity.ok(service.createProduct(request));
        // Using CQRS
        Integer id = (Integer) new java.util.Random().nextInt(1000000); // Simple ID generation for demo
        commandGateway.sendAndWait(org.projet.ecommerce.product.product.cqrs.commands.CreateProductCommand.builder()
                .id(id)
                .name(request.name())
                .description(request.description())
                .availableQuantity(request.availableQuantity())
                .price(request.price())
                .categoryId(request.categoryId())
                .build());
        return ResponseEntity.ok(id);
    }

    @PostMapping("/purchase")
    public ResponseEntity<List<ProductPurchaseResponse>> purchaseProducts(
            @RequestBody List<ProductPurchaseRequest> request
    ) {
        // For purchase, we might want to keep the service logic or use commands for each product
        // To keep it simple and consistent with CQRS, we can send commands
        for (ProductPurchaseRequest purchaseRequest : request) {
            commandGateway.sendAndWait(new org.projet.ecommerce.product.product.cqrs.commands.UpdateProductQuantityCommand(
                    purchaseRequest.productId(),
                    purchaseRequest.quantity()
            ));
        }
        return ResponseEntity.ok(service.purchaseProducts(request)); // Still using service for the response mapping for now
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findById(
            @PathVariable("product-id") Integer productId
    ) {
        // return ResponseEntity.ok(service.findById(productId));
        // Using CQRS
        return ResponseEntity.ok(queryGateway.query(
                new org.projet.ecommerce.product.product.cqrs.projections.GetProductByIdQuery(productId),
                org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf(ProductResponse.class)
        ).join());
    }

    @GetMapping
    @Retry(name = "myRetry", fallbackMethod = "fallback")
    @RateLimiter(name = "myRateLimiter", fallbackMethod = "fallback")
    @CircuitBreaker(name = "productmicroService", fallbackMethod = "fallback")
    public ResponseEntity<List<ProductResponse>> findAll() {
        // return ResponseEntity.ok(service.findAll());
        // Using CQRS
        return ResponseEntity.ok((List<ProductResponse>) queryGateway.query(
                new org.projet.ecommerce.product.product.cqrs.projections.GetAllProductsQuery(),
                org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf(List.class)
        ).join());
    }

    public ResponseEntity<List<ProductResponse>> fallback(Exception e) {
        // Log the exception for debugging
        System.err.println("Fallback triggered: " + e.getMessage());
        // Return an empty list as fallback
        return ResponseEntity.ok(Collections.emptyList());
    }
}
