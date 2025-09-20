package com.ecommerce.notification.dto;

import com.ecommerce.notification.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDto {
    private Long id;
    private Long recipientId;
    private String recipient;
    private Notification.NotificationType type;
    private Notification.NotificationChannel channel;
    private String subject;
    private String content;
    private Notification.NotificationStatus status;
    private String eventType;
    private String eventId;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
