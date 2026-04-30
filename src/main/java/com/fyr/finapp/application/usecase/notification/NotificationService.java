package com.fyr.finapp.application.usecase.notification;

import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public class NotificationService {

    private final IAuthenticationRepository authenticationRepository;
    private final INotificationRepository notificationRepository;

    public NotificationService(IAuthenticationRepository authenticationRepository,
                                INotificationRepository notificationRepository) {
        this.authenticationRepository = authenticationRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<INotificationRepository.NotificationItem> listRecent(int limit) {
        var userId = authenticationRepository.getCurrentUserId();
        return notificationRepository.findRecent(userId.value(), Math.min(limit, 100));
    }

    public long countUnread() {
        var userId = authenticationRepository.getCurrentUserId();
        return notificationRepository.countUnread(userId.value());
    }

    @Transactional
    public void markRead(UUID notificationId) {
        var userId = authenticationRepository.getCurrentUserId();
        notificationRepository.markRead(notificationId, userId.value());
    }

    @Transactional
    public void markAllRead() {
        var userId = authenticationRepository.getCurrentUserId();
        notificationRepository.markAllRead(userId.value());
    }
}
