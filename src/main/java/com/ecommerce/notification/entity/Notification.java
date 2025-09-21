package com.ecommerce.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum NotificationType {
        ORDER_CONFIRMATION, ORDER_SHIPPED, ORDER_DELIVERED, ORDER_CANCELLED,
        USER_REGISTRATION, USER_PASSWORD_RESET, PAYMENT_SUCCESS, PAYMENT_FAILED,
        INVENTORY_LOW_STOCK, PROMOTIONAL_OFFER, GENERAL
    }

    public enum NotificationChannel {
        EMAIL, SMS, PUSH_NOTIFICATION, IN_APP
    }

    public enum NotificationStatus {
        PENDING, SENT, FAILED, CANCELLED
    }
}
