package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.AcceptInvitationUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountInvitationRepository;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class AcceptInvitationService implements AcceptInvitationUseCase {
    private static final Logger log = LoggerFactory.getLogger(AcceptInvitationService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final IAccountInvitationRepository invitationRepository;
    private final IAccountRepository accountRepository;
    private final INotificationRepository notificationRepository;

    public AcceptInvitationService(
            IAuthenticationRepository authenticationRepository,
            IAccountInvitationRepository invitationRepository,
            IAccountRepository accountRepository,
            INotificationRepository notificationRepository) {
        this.authenticationRepository = authenticationRepository;
        this.invitationRepository = invitationRepository;
        this.accountRepository = accountRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void accept(String invitationId) {
        var userId = authenticationRepository.getCurrentUserId();
        var id = UUID.fromString(invitationId);

        var invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invitation not found", null));

        if (!invitation.inviteeId().equals(userId.value())) {
            throw new ForbiddenException("Not your invitation", null);
        }
        if (!"PENDING".equals(invitation.status())) {
            throw new ValidationException("Invitation is no longer pending", null);
        }

        invitationRepository.accept(id);
        accountRepository.addMember(
                AccountId.of(invitation.accountId().toString()),
                userId,
                new UserId(invitation.inviterId())
        );

        try {
            notificationRepository.save(new INotificationRepository.SaveCommand(
                    invitation.inviterId(), "ACCOUNT_JOINED",
                    "Un usuario aceptó tu invitación",
                    "Ahora tiene acceso a tu cuenta compartida",
                    Map.of("accountId", invitation.accountId().toString())
            ));
        } catch (Exception e) {
            log.warn("Failed to send notification for invitation accept {}", invitationId, e);
        }
    }
}
