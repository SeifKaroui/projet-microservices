package org.projet.ecommerce.product.product.cqrs.projections;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.projet.ecommerce.product.category.Category;
import org.projet.ecommerce.product.product.Product;
import org.projet.ecommerce.product.product.ProductRepository;
import org.projet.ecommerce.product.product.ProductResponse;
import org.projet.ecommerce.product.product.ProductMapper;
import org.projet.ecommerce.product.product.cqrs.events.ProductCreatedEvent;
import org.projet.ecommerce.product.product.cqrs.events.ProductQuantityUpdatedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductProjection {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final jakarta.persistence.EntityManager entityManager;

    @EventHandler
    @org.springframework.transaction.annotation.Transactional
    public void on(ProductCreatedEvent event) {
        Product product = Product.builder()
                .id(event.id())
                .name(event.name())
                .description(event.description())
                .availableQuantity(event.availableQuantity())
                .price(event.price())
                .categoryId(event.categoryId())
                .build();
        entityManager.persist(product);
    }

    @EventHandler
    public void on(ProductQuantityUpdatedEvent event) {
        Product product = repository.findById(event.id())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setAvailableQuantity(product.getAvailableQuantity() - event.quantity());
        repository.save(product);
    }

    @QueryHandler
    public java.util.List handle(GetAllProductsQuery query) {
        return repository.findAll().stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public ProductResponse handle(GetProductByIdQuery query) {
        return repository.findById(query.id())
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
