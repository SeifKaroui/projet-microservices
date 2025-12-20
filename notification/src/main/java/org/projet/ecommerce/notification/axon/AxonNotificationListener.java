package org.projet.ecommerce.notification.axon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.projet.ecommerce.notification.email.EmailService;
import org.projet.ecommerce.notification.notification.Notification;
import org.projet.ecommerce.notification.notification.NotificationRepository;
import org.projet.ecommerce.payment.payment.cqrs.events.PaymentCreatedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.projet.ecommerce.notification.notification.NotificationType.PAYMENT_CONFIRMATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class AxonNotificationListener {

    private final NotificationRepository repository;
    private final EmailService emailService;

    @EventHandler
    public void on(PaymentCreatedEvent event) {
        log.info("Consuming PaymentCreatedEvent via Axon: {}", event);
        
        org.projet.ecommerce.notification.kafka.payment.PaymentConfirmation paymentConfirmation = 
            new org.projet.ecommerce.notification.kafka.payment.PaymentConfirmation(
                event.orderReference(),
                event.amount(),
                null, // paymentMethod is null for now as we use Object in event
                event.customerFirstname(),
                event.customerLastname(),
                event.customerEmail()
            );

        repository.save(
                Notification.builder()
                        .type(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );
        
        try {
            var customerName = event.customerFirstname() + " " + event.customerLastname();
            emailService.sendPaymentSuccessEmail(
                    event.customerEmail(),
                    customerName,
                    event.amount(),
                    event.orderReference()
            );
        } catch (Exception e) {
            log.error("Error sending email for PaymentCreatedEvent", e);
        }
    }
}
