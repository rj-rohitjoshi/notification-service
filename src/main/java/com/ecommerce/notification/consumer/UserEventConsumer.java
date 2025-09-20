package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.SendNotificationRequest;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.event.UserRegisteredEvent;
import com.ecommerce.notification.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void handleUserRegistered(JsonNode eventData) {
        try {
            log.info("Received user registered event: {}", eventData);

            UserRegisteredEvent event = objectMapper.treeToValue(eventData, UserRegisteredEvent.class);

            String subject = "Welcome to Our E-Commerce Platform!";
            String content = String.format(
                    "Hi %s,\n\n" +
                            "Welcome to our e-commerce platform!\n" +
                            "Your account has been successfully created with username: %s\n\n" +
                            "Start exploring our amazing products and deals.\n\n" +
                            "Happy Shopping!\n" +
                            "E-Commerce Team",
                    event.getFirstName() != null ? event.getFirstName() : event.getUsername(),
                    event.getUsername()
            );

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .recipientId(event.getUserId())
                    .recipient(event.getEmail())
                    .type(Notification.NotificationType.USER_REGISTRATION)
                    .channel(Notification.NotificationChannel.EMAIL)
                    .subject(subject)
                    .content(content)
                    .eventType("user.registered")
                    .eventId(event.getUserId().toString())
                    .build();

            notificationService.sendNotification(request);

        } catch (Exception e) {
            log.error("Error processing user registered event", e);
        }
    }
}
