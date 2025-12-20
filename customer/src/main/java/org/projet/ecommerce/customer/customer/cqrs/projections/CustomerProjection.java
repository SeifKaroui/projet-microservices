package org.projet.ecommerce.customer.customer.cqrs.projections;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.projet.ecommerce.customer.customer.Customer;
import org.projet.ecommerce.customer.customer.CustomerRepository;
import org.projet.ecommerce.customer.customer.CustomerResponse;
import org.projet.ecommerce.customer.customer.CustomerMapper;
import org.projet.ecommerce.customer.customer.cqrs.events.CustomerCreatedEvent;
import org.projet.ecommerce.customer.customer.cqrs.events.CustomerUpdatedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerProjection {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        Customer customer = Customer.builder()
                .id(event.id())
                .firstname(event.firstname())
                .lastname(event.lastname())
                .email(event.email())
                .address(event.address())
                .build();
        repository.save(customer);
    }

    @EventHandler
    public void on(CustomerUpdatedEvent event) {
        repository.findById(event.id()).ifPresent(customer -> {
            customer.setFirstname(event.firstname());
            customer.setLastname(event.lastname());
            customer.setEmail(event.email());
            customer.setAddress(event.address());
            repository.save(customer);
        });
    }

    @QueryHandler
    public List<CustomerResponse> handle(GetAllCustomersQuery query) {
        return repository.findAll().stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public CustomerResponse handle(GetCustomerByIdQuery query) {
        return repository.findById(query.id())
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
