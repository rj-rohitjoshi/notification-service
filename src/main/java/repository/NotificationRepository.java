package com.ecommerce.notification.repository;

import com.ecommerce.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientId(Long recipientId);
    List<Notification> findByRecipient(String recipient);
    List<Notification> findByStatus(Notification.NotificationStatus status);
    List<Notification> findByType(Notification.NotificationType type);
    List<Notification> findByChannel(Notification.NotificationChannel channel);
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);
    Long countByStatusAndRecipientId(Notification.NotificationStatus status, Long recipientId);
}
