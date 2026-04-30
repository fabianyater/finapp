package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.NotificationEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.NotificationJpaRepository;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class NotificationAdapter implements INotificationRepository {
    private static final Logger log = LoggerFactory.getLogger(NotificationAdapter.class);

    private final NotificationJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(SaveCommand command) {
        var entity = new NotificationEntity();
        entity.setUserId(command.userId());
        entity.setType(command.type());
        entity.setTitle(command.title());
        entity.setBody(command.body());
        entity.setMetadata(toJson(command.metadata()));
        jpaRepository.save(entity);
    }

    @Override
    public List<NotificationItem> findRecent(UUID userId, int limit) {
        return jpaRepository.findRecentByUserId(userId, limit)
                .stream()
                .map(this::toItem)
                .toList();
    }

    @Override
    public long countUnread(UUID userId) {
        return jpaRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markRead(UUID notificationId, UUID userId) {
        jpaRepository.markRead(notificationId, userId, OffsetDateTime.now());
    }

    @Override
    @Transactional
    public void markAllRead(UUID userId) {
        jpaRepository.markAllRead(userId, OffsetDateTime.now());
    }

    private NotificationItem toItem(NotificationEntity e) {
        return new NotificationItem(
                e.getId(), e.getType(), e.getTitle(), e.getBody(),
                fromJson(e.getMetadata()),
                e.getReadAt() != null ? e.getReadAt().toInstant() : null,
                e.getCreatedAt().toInstant()
        );
    }

    private String toJson(Map<String, Object> metadata) {
        if (metadata == null) return null;
        try { return objectMapper.writeValueAsString(metadata); }
        catch (JsonProcessingException ex) { log.warn("Failed to serialize notification metadata", ex); return "{}"; }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fromJson(String json) {
        if (json == null) return Collections.emptyMap();
        try { return objectMapper.readValue(json, Map.class); }
        catch (JsonProcessingException ex) { log.warn("Failed to deserialize notification metadata", ex); return Collections.emptyMap(); }
    }
}
