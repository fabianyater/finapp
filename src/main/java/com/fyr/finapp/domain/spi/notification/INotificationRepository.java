package com.fyr.finapp.domain.spi.notification;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface INotificationRepository {

    record SaveCommand(
            UUID userId,
            String type,
            String title,
            String body,
            Map<String, Object> metadata
    ) {}

    record NotificationItem(
            UUID id,
            String type,
            String title,
            String body,
            Map<String, Object> metadata,
            Instant readAt,
            Instant createdAt
    ) {
        public boolean isUnread() { return readAt == null; }
    }

    void save(SaveCommand command);

    List<NotificationItem> findRecent(UUID userId, int limit);

    long countUnread(UUID userId);

    void markRead(UUID notificationId, UUID userId);

    void markAllRead(UUID userId);
}
