package org.projet.ecommerce.payment.payment.cqrs.projections;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.projet.ecommerce.payment.notification.NotificationProducer;
import org.projet.ecommerce.payment.notification.PaymentNotificationRequest;
import org.projet.ecommerce.payment.payment.Payment;
import org.projet.ecommerce.payment.payment.PaymentRepository;
import org.projet.ecommerce.payment.payment.cqrs.events.PaymentCreatedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProjection {

    private final PaymentRepository repository;
    private final NotificationProducer notificationProducer;

    @EventHandler
    public void on(PaymentCreatedEvent event) {
        Payment payment = Payment.builder()
                .id(event.id())
                .amount(event.amount())
                .paymentMethod(event.paymentMethod())
                .orderId(event.orderId())
                .build();
        repository.save(payment);

        notificationProducer.sendNotification(new PaymentNotificationRequest(
                event.orderReference(),
                event.amount(),
                event.paymentMethod(),
                event.customerFirstname(),
                event.customerLastname(),
                event.customerEmail()
        ));
    }
}
