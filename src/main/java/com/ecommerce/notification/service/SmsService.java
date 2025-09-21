package com.ecommerce.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    @Value("${notification.sms.provider}")
    private String smsProvider;

    @Value("${notification.sms.account-sid}")
    private String accountSid;

    @Value("${notification.sms.auth-token}")
    private String authToken;

    @Value("${notification.sms.from-number}")
    private String fromNumber;

    public boolean sendSms(String to, String content) {
        try {
            // For demo purposes, we'll just log the SMS
            // In production, integrate with Twilio, AWS SNS, or other SMS providers
            log.info("SMS would be sent to: {} with content: {}", to, content);
            log.info("Using provider: {} from number: {}", smsProvider, fromNumber);

            // Simulate SMS sending
            return true;

        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", to, e);
            return false;
        }
    }
}
