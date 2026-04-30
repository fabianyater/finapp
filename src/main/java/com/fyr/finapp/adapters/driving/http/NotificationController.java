package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.application.usecase.notification.NotificationService;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.base-path}/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    record NotificationResponse(UUID id, String type, String title, String body,
                                Map<String, Object> metadata, boolean unread,
                                Instant createdAt) {}

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(
            @RequestParam(defaultValue = "50") int limit) {
        var items = notificationService.listRecent(limit);
        return ResponseEntity.ok(items.stream().map(this::toResponse).toList());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        notificationService.markRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllRead();
        return ResponseEntity.noContent().build();
    }

    private NotificationResponse toResponse(INotificationRepository.NotificationItem item) {
        return new NotificationResponse(item.id(), item.type(), item.title(), item.body(),
                item.metadata(), item.isUnread(), item.createdAt());
    }
}
