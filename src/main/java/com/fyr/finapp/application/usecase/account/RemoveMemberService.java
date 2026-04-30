package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.RemoveMemberUseCase;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RemoveMemberService implements RemoveMemberUseCase {
    private static final Logger log = LoggerFactory.getLogger(RemoveMemberService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final IAccountRepository accountRepository;
    private final AccountValidator accountValidator;
    private final INotificationRepository notificationRepository;

    public RemoveMemberService(
            IAuthenticationRepository authenticationRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator,
            INotificationRepository notificationRepository) {
        this.authenticationRepository = authenticationRepository;
        this.accountRepository = accountRepository;
        this.accountValidator = accountValidator;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void remove(String accountId, String memberUserId) {
        var requesterId = authenticationRepository.getCurrentUserId();
        var accId = AccountId.of(accountId);
        var memberId = UserId.of(memberUserId);

        var account = accountValidator.getAccountAndValidateOwnership(accId, requesterId);

        if (account.getUserId().equals(memberId)) {
            throw new ValidationException(
                    "Cannot remove the account owner",
                    AccountErrorCode.CANNOT_REMOVE_OWNER
            );
        }

        if (!accountRepository.isMember(accId, memberId)) {
            throw new NotFoundException(
                    "Member not found in this account",
                    AccountErrorCode.MEMBER_NOT_FOUND
            );
        }

        accountRepository.removeMember(accId, memberId);

        try {
            notificationRepository.save(new INotificationRepository.SaveCommand(
                    memberId.value(), "ACCOUNT_REMOVED",
                    "Te han eliminado de una cuenta",
                    "Ya no tienes acceso a la cuenta \"" + account.getName().value() + "\"",
                    Map.of("accountId", accId.value().toString(), "accountName", account.getName().value())
            ));
        } catch (Exception e) {
            log.warn("Failed to save notification for member removal accountId={}", accountId, e);
        }
    }
}
