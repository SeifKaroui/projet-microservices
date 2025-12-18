package org.projet.ecommerce.notification.notification;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository repository;

  public List<Notification> findAllNotifications() {
    return repository.findAll();
  }
}

