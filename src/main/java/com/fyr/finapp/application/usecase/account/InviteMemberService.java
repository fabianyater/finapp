package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.InviteMemberUseCase;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;
import com.fyr.finapp.domain.spi.account.IAccountInvitationRepository;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class InviteMemberService implements InviteMemberUseCase {
    private static final Logger log = LoggerFactory.getLogger(InviteMemberService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final IAccountRepository accountRepository;
    private final IUserRepository userRepository;
    private final AccountValidator accountValidator;
    private final IAccountInvitationRepository invitationRepository;
    private final INotificationRepository notificationRepository;

    public InviteMemberService(
            IAuthenticationRepository authenticationRepository,
            IAccountRepository accountRepository,
            IUserRepository userRepository,
            AccountValidator accountValidator,
            IAccountInvitationRepository invitationRepository,
            INotificationRepository notificationRepository) {
        this.authenticationRepository = authenticationRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountValidator = accountValidator;
        this.invitationRepository = invitationRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void invite(String accountId, String email) {
        var inviterId = authenticationRepository.getCurrentUserId();
        var accId = AccountId.of(accountId);

        var account = accountValidator.getAccountAndValidateOwnership(accId, inviterId);

        var targetUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        "User not found with email=" + email,
                        UserErrorCode.USER_NOT_FOUND
                ));

        if (targetUser.getId().equals(inviterId)) {
            throw new ValidationException("Cannot invite yourself to the account", AccountErrorCode.CANNOT_INVITE_SELF);
        }

        if (accountRepository.isMember(accId, targetUser.getId())) {
            throw new ValidationException("User is already a member of this account", AccountErrorCode.ALREADY_MEMBER);
        }

        if (invitationRepository.hasPendingInvite(accId.value(), targetUser.getId().value())) {
            throw new ValidationException("User already has a pending invitation for this account", AccountErrorCode.ALREADY_MEMBER);
        }

        var invitationId = invitationRepository.save(accId.value(), inviterId.value(), targetUser.getId().value());

        try {
            notificationRepository.save(new INotificationRepository.SaveCommand(
                    targetUser.getId().value(), "ACCOUNT_INVITE",
                    "Te han invitado a una cuenta",
                    "\"" + account.getName().value() + "\" · Toca para aceptar o rechazar",
                    Map.of(
                            "invitationId", invitationId.toString(),
                            "accountId", accId.value().toString(),
                            "accountName", account.getName().value()
                    )
            ));
        } catch (Exception e) {
            log.warn("Failed to save notification for member invite accountId={}", accountId, e);
        }
    }
}
