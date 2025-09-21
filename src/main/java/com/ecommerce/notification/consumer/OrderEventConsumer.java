package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.SendNotificationRequest;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.event.OrderCreatedEvent;
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
public class OrderEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.created", groupId = "notification-service")
    public void handleOrderCreated(JsonNode eventData) {
        try {
            log.info("Received order created event: {}", eventData);

            OrderCreatedEvent event = objectMapper.treeToValue(eventData, OrderCreatedEvent.class);

            String subject = "Order Confirmation - " + event.getOrderNumber();
            String content = String.format(
                    "Dear Customer,\n\n" +
                            "Your order %s has been successfully placed!\n" +
                            "Order Total: $%.2f\n" +
                            "We'll notify you once your order is shipped.\n\n" +
                            "Thank you for shopping with us!\n" +
                            "E-Commerce Team",
                    event.getOrderNumber(),
                    event.getTotalAmount()
            );

            // For demo purposes, we'll use a dummy email
            // In production, you'd fetch user details from user-service
            String userEmail = "user" + event.getUserId() + "@example.com";

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .recipientId(event.getUserId())
                    .recipient(userEmail)
                    .type(Notification.NotificationType.ORDER_CONFIRMATION)
                    .channel(Notification.NotificationChannel.EMAIL)
                    .subject(subject)
                    .content(content)
                    .eventType("order.created")
                    .eventId(event.getOrderId().toString())
                    .build();

            notificationService.sendNotification(request);

        } catch (Exception e) {
            log.error("Error processing order created event", e);
        }
    }
}
