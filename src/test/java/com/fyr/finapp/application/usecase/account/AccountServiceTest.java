package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.CreateAccountUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    @Test
    void shouldCreateAccountSuccessfully() {
        // Arrange
        IAccountRepository accountRepository = mock(IAccountRepository.class);
        IAuthenticationRepository authenticationRepository = mock(IAuthenticationRepository.class);
        AccountService accountService = new AccountService(accountRepository, authenticationRepository);

        UserId userId = new UserId(UUID.randomUUID());
        CreateAccountUseCase.Command command = new CreateAccountUseCase.Command(
                "Savings",
                "BANK",
                1000L,
                "icon-bank",
                "#FFFFFF",
                "USD"
        );

        when(authenticationRepository.getCurrentUserId()).thenReturn(userId);
        when(accountRepository.existsByUserIdAndName(any(), any())).thenReturn(false);
        when(accountRepository.findByUserId(userId)).thenReturn(List.of());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        CreateAccountUseCase.Result result = accountService.create(command);

        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(command.name(), savedAccount.getName().value());
        assertEquals(AccountType.BANK, savedAccount.getType());
        assertEquals(1000L, savedAccount.getInitialBalance().amount());
    }

    @Test
    void shouldThrowValidationExceptionForDuplicateAccountName() {
        IAccountRepository accountRepository = mock(IAccountRepository.class);
        IAuthenticationRepository authenticationRepository = mock(IAuthenticationRepository.class);
        AccountService accountService = new AccountService(accountRepository, authenticationRepository);

        UserId userId = new UserId(UUID.randomUUID());
        CreateAccountUseCase.Command command = new CreateAccountUseCase.Command("Expenses", "CASH", 500L, "icon-cash", "#000000", "USD");

        when(authenticationRepository.getCurrentUserId()).thenReturn(userId);
        when(accountRepository.existsByUserIdAndName(userId, new AccountName(command.name()))).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> accountService.create(command));
        assertEquals(AccountErrorCode.NAME_ALREADY_EXISTS, exception.getCode());
        assertTrue(exception.getMessage().contains(command.name()));
    }

    @Test
    void shouldThrowValidationExceptionForNegativeBalanceOnCashAccount() {
        IAccountRepository accountRepository = mock(IAccountRepository.class);
        IAuthenticationRepository authenticationRepository = mock(IAuthenticationRepository.class);
        AccountService accountService = new AccountService(accountRepository, authenticationRepository);

        UserId userId = new UserId(UUID.randomUUID());
        CreateAccountUseCase.Command command = new CreateAccountUseCase.Command("Wallet", "CASH", -100L, "icon-wallet", "#AAAAAA", "USD");

        when(authenticationRepository.getCurrentUserId()).thenReturn(userId);

        ValidationException exception = assertThrows(ValidationException.class, () -> accountService.create(command));
        assertEquals(AccountErrorCode.INVALID_INITIAL_BALANCE, exception.getCode());
        assertTrue(exception.getMessage().contains("CASH accounts should not have negative initial balance"));
    }

    @Test
    void shouldMarkAccountAsDefaultWhenNoExistingAccounts() {
        IAccountRepository accountRepository = mock(IAccountRepository.class);
        IAuthenticationRepository authenticationRepository = mock(IAuthenticationRepository.class);
        AccountService accountService = new AccountService(accountRepository, authenticationRepository);

        UserId userId = new UserId(UUID.randomUUID());
        CreateAccountUseCase.Command command = new CreateAccountUseCase.Command(
                "Primary",
                "BANK",
                1000L,
                "icon-primary",
                "#123456",
                "USD"
        );

        when(authenticationRepository.getCurrentUserId()).thenReturn(userId);
        when(accountRepository.existsByUserIdAndName(any(), any())).thenReturn(false);
        when(accountRepository.findByUserId(userId)).thenReturn(List.of());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        accountService.create(command);

        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertTrue(savedAccount.isDefaultAccount(), "Account should be marked as default");
    }

    @Test
    void shouldNotMarkAccountAsDefaultWhenExistingAccountsPresent() {
        IAccountRepository accountRepository = mock(IAccountRepository.class);
        IAuthenticationRepository authenticationRepository = mock(IAuthenticationRepository.class);
        AccountService accountService = new AccountService(accountRepository, authenticationRepository);

        UserId userId = new UserId(UUID.randomUUID());
        CreateAccountUseCase.Command command = new CreateAccountUseCase.Command(
                "Secondary",
                "BANK",
                500L,
                "icon-secondary",
                "#654321",
                "USD"
        );
        Account existingAccount = mock(Account.class);

        when(authenticationRepository.getCurrentUserId()).thenReturn(userId);
        when(accountRepository.existsByUserIdAndName(any(), any())).thenReturn(false);
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(existingAccount));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        accountService.create(command);

        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertFalse(savedAccount.isDefaultAccount(), "Account should NOT be marked as default when other accounts exist");
    }
}