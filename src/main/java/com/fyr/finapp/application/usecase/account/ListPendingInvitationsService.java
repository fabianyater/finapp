package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.ListPendingInvitationsUseCase;
import com.fyr.finapp.domain.spi.account.IAccountInvitationRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;

import java.util.List;

public class ListPendingInvitationsService implements ListPendingInvitationsUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final IAccountInvitationRepository invitationRepository;

    public ListPendingInvitationsService(
            IAuthenticationRepository authenticationRepository,
            IAccountInvitationRepository invitationRepository) {
        this.authenticationRepository = authenticationRepository;
        this.invitationRepository = invitationRepository;
    }

    @Override
    public List<InvitationResult> list() {
        var userId = authenticationRepository.getCurrentUserId();
        return invitationRepository.findPending(userId.value()).stream()
                .map(v -> new InvitationResult(
                        v.id().toString(),
                        v.accountId().toString(),
                        v.accountName(),
                        v.inviterName(),
                        v.inviterEmail(),
                        v.createdAt()
                ))
                .toList();
    }
}
