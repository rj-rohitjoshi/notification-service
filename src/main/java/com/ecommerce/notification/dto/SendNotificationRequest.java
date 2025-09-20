package com.ecommerce.notification.dto;

import com.ecommerce.notification.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SendNotificationRequest {
    private Long recipientId;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotNull(message = "Notification type is required")
    private Notification.NotificationType type;

    @NotNull(message = "Notification channel is required")
    private Notification.NotificationChannel channel;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    private String eventType;
    private String eventId;
}
