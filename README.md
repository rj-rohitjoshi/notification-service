# Notification Service

Event-driven Spring Boot microservice for handling notifications via email, SMS, push notifications, and in-app messaging.

## Build and Run

mvn clean package
mvn spring-boot:run

or
docker build -t notification-service .
docker run -p 8086:8086 notification-service


## Features

- Event-driven architecture with Kafka consumers
- Multi-channel notifications (Email, SMS, Push, In-App)
- Notification history and status tracking
- Retry failed notifications
- Email integration with Spring Mail
- SMS integration (Twilio ready)
- H2 database for notification storage

## Kafka Topics Consumed

- `order.created` - Order confirmation notifications
- `user.registered` - Welcome notifications
- Custom events can be added easily

## REST API

- `POST /notifications` - send notification manually
- `GET /notifications/{id}` - get notification by ID
- `GET /notifications/recipient/{recipientId}` - get user notifications (paginated)
- `GET /notifications/status/{status}` - get notifications by status
- `GET /notifications/recipient/{recipientId}/unread-count` - get unread count
- `POST /notifications/{id}/retry` - retry failed notification
- `PATCH /notifications/{id}/cancel` - cancel pending notification

## Configuration

Configure email and SMS providers in `application.yml`:

spring.mail.host: your-smtp-host
notification.sms.account-sid: your-twilio-sid


## Test

mvn test

Uses H2 for tests and embedded Kafka for integration tests.

---

