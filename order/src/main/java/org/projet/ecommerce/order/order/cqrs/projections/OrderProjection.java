package org.projet.ecommerce.order.order.cqrs.projections;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.projet.ecommerce.order.order.Order;
import org.projet.ecommerce.order.order.OrderRepository;
import org.projet.ecommerce.order.order.OrderResponse;
import org.projet.ecommerce.order.order.OrderMapper;
import org.projet.ecommerce.order.order.cqrs.events.OrderCreatedEvent;
import org.projet.ecommerce.order.orderline.OrderLineRequest;
import org.projet.ecommerce.order.orderline.OrderLineService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderProjection {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        Order order = Order.builder()
                .id(event.id())
                .reference(event.reference())
                .totalAmount(event.amount())
                .paymentMethod(event.paymentMethod())
                .customerId(event.customerId())
                .build();
        repository.save(order);

        event.products().forEach(product -> {
            orderLineService.saveOrderLine(new OrderLineRequest(
                    null,
                    event.id(),
                    product.productId(),
                    product.quantity()
            ));
        });
    }

    @QueryHandler
    public List<OrderResponse> handle(GetAllOrdersQuery query) {
        return repository.findAll().stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public OrderResponse handle(GetOrderByIdQuery query) {
        return repository.findById(query.id())
                .map(mapper::fromOrder)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
