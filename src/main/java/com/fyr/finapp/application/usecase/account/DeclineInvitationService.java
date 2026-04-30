package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.DeclineInvitationUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.spi.account.IAccountInvitationRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import jakarta.transaction.Transactional;

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
        var id = UUID.fromString(invitationId);

        var invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invitation not found", null));

        if (!invitation.inviteeId().equals(userId.value())) {
            throw new ForbiddenException("Not your invitation", null);
        }
        if (!"PENDING".equals(invitation.status())) {
            throw new ValidationException("Invitation is no longer pending", null);
        }

        invitationRepository.decline(id);
    }
}
