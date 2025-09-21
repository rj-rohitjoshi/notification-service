package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationDto;
import com.ecommerce.notification.dto.SendNotificationRequest;
import com.ecommerce.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationDto sendNotification(SendNotificationRequest request);
    NotificationDto getNotificationById(Long id);
    List<NotificationDto> getNotificationsByRecipientId(Long recipientId);
    List<NotificationDto> getNotificationsByRecipient(String recipient);
    Page<NotificationDto> getNotificationsByRecipientId(Long recipientId, Pageable pageable);
    List<NotificationDto> getNotificationsByStatus(Notification.NotificationStatus status);
    void retryFailedNotification(Long id);
    void cancelNotification(Long id);
    Long getUnreadCount(Long recipientId);
}
