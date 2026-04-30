package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.ListMembersUseCase;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;

import java.util.List;

public class ListMembersService implements ListMembersUseCase {
    private final IAuthenticationRepository authenticationRepository;
    private final IAccountRepository accountRepository;
    private final AccountValidator accountValidator;

    public ListMembersService(
            IAuthenticationRepository authenticationRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        this.authenticationRepository = authenticationRepository;
        this.accountRepository = accountRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    public List<MemberResult> list(String accountId) {
        var userId = authenticationRepository.getCurrentUserId();
        var accId = AccountId.of(accountId);

        accountValidator.getAccountAndValidateAccess(accId, userId);

        return accountRepository.findMembers(accId)
                .stream()
                .map(m -> new MemberResult(m.userId(), m.email(), m.name(), m.joinedAt()))
                .toList();
    }
}
