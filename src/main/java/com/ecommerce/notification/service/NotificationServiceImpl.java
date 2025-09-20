package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationDto;
import com.ecommerce.notification.dto.SendNotificationRequest;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Override
    public NotificationDto sendNotification(SendNotificationRequest request) {
        log.info("Sending notification to: {} via {}", request.getRecipient(), request.getChannel());

        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .recipient(request.getRecipient())
                .type(request.getType())
                .channel(request.getChannel())
                .subject(request.getSubject())
                .content(request.getContent())
                .eventType(request.getEventType())
                .eventId(request.getEventId())
                .status(Notification.NotificationStatus.PENDING)
                .build();

        try {
            boolean sent = false;
            switch (request.getChannel()) {
                case EMAIL:
                    sent = emailService.sendEmail(request.getRecipient(), request.getSubject(), request.getContent());
                    break;
                case SMS:
                    sent = smsService.sendSms(request.getRecipient(), request.getContent());
                    break;
                case PUSH_NOTIFICATION:
                case IN_APP:
                    // For now, we'll mark these as sent (implement actual push notification logic here)
                    sent = true;
                    break;
            }

            if (sent) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("Notification sent successfully to: {}", request.getRecipient());
            } else {
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to send notification");
                log.error("Failed to send notification to: {}", request.getRecipient());
            }

        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            log.error("Error sending notification to: {}", request.getRecipient(), e);
        }

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDto(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
        return convertToDto(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByRecipientId(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsByRecipientId(Long recipientId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
        return notifications.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByStatus(Notification.NotificationStatus status) {
        return notificationRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void retryFailedNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        if (notification.getStatus() != Notification.NotificationStatus.FAILED) {
            throw new IllegalStateException("Can only retry failed notifications");
        }

        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId(notification.getRecipientId())
                .recipient(notification.getRecipient())
                .type(notification.getType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .eventType(notification.getEventType())
                .eventId(notification.getEventId())
                .build();

        sendNotification(request);
    }

    @Override
    public void cancelNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        if (notification.getStatus() == Notification.NotificationStatus.PENDING) {
            notification.setStatus(Notification.NotificationStatus.CANCELLED);
            notificationRepository.save(notification);
            log.info("Notification cancelled with id: {}", id);
        } else {
            throw new IllegalStateException("Can only cancel pending notifications");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long recipientId) {
        return notificationRepository.countByStatusAndRecipientId(Notification.NotificationStatus.SENT, recipientId);
    }

    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientId())
                .recipient(notification.getRecipient())
                .type(notification.getType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .status(notification.getStatus())
                .eventType(notification.getEventType())
                .eventId(notification.getEventId())
                .errorMessage(notification.getErrorMessage())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
