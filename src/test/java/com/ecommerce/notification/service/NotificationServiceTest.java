package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationDto;
import com.ecommerce.notification.dto.SendNotificationRequest;
import com.ecommerce.notification.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    void sendNotificationSuccessfully() {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId(1L)
                .recipient("test@example.com")
                .type(Notification.NotificationType.ORDER_CONFIRMATION)
                .channel(Notification.NotificationChannel.EMAIL)
                .subject("Test Notification")
                .content("This is a test notification")
                .eventType("test.event")
                .eventId("123")
                .build();

        NotificationDto sent = notificationService.sendNotification(request);

        assertThat(sent.getId()).isNotNull();
        assertThat(sent.getRecipient()).isEqualTo("test@example.com");
        assertThat(sent.getSubject()).isEqualTo("Test Notification");
        assertThat(sent.getStatus()).isEqualTo(Notification.NotificationStatus.SENT);
    }

    @Test
    void getNotificationsByRecipientIdWorks() {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId(2L)
                .recipient("user2@example.com")
                .type(Notification.NotificationType.USER_REGISTRATION)
                .channel(Notification.NotificationChannel.EMAIL)
                .subject("Welcome")
                .content("Welcome message")
                .build();

        notificationService.sendNotification(request);

        var notifications = notificationService.getNotificationsByRecipientId(2L);
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getRecipientId()).isEqualTo(2L);
    }
}
