package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.DeclineInvitationUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.spi.account.IAccountInvitationRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import jakarta.transaction.Transactional;

import com.fyr.finapp.domain.exception.ValidationException;
import java.util.UUID;

public class DeclineInvitationService implements DeclineInvitationUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final IAccountInvitationRepository invitationRepository;

    public DeclineInvitationService(
            IAuthenticationRepository authenticationRepository,
            IAccountInvitationRepository invitationRepository) {
        this.authenticationRepository = authenticationRepository;
        this.invitationRepository = invitationRepository;
    }

    @Override
    @Transactional
    public void decline(String invitationId) {
        var userId = authenticationRepository.getCurrentUserId();
        var id = parseInvitationId(invitationId);

        var invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invitation not found", AccountErrorCode.INVITATION_NOT_FOUND));

        if (!invitation.inviteeId().equals(userId.value())) {
            throw new ForbiddenException("Not your invitation", AccountErrorCode.INVITATION_FORBIDDEN);
        }
        if (!"PENDING".equals(invitation.status())) {
            throw new ValidationException("Invitation is no longer pending", AccountErrorCode.INVITATION_NOT_PENDING);
        }

        invitationRepository.decline(id);
    }

    private UUID parseInvitationId(String invitationId) {
        try {
            return UUID.fromString(invitationId);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid invitation ID format: " + invitationId, AccountErrorCode.INVITATION_NOT_FOUND);
        }
    }
}
