package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import lombok.NonNull;

public class AccountValidator {
    private final IAccountRepository accountRepository;

    public AccountValidator(IAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Obtiene una cuenta por ID y valida que pertenezca al usuario especificado.
     *
     * @param accountId ID de la cuenta a buscar
     * @param userId ID del usuario propietario
     * @return La cuenta encontrada
     * @throws NotFoundException si la cuenta no existe
     * @throws ForbiddenException si la cuenta no pertenece al usuario
     */
    public @NonNull Account getAccountAndValidateOwnership(AccountId accountId, UserId userId) {
        Account account = getAccountOrThrow(accountId);
        validateOwnership(account, userId);
        return account;
    }

    /**
     * Busca una cuenta por ID o lanza excepción si no existe.
     *
     * @param accountId ID de la cuenta a buscar
     * @return La cuenta encontrada
     * @throws NotFoundException si la cuenta no existe
     */
    public @NonNull Account getAccountOrThrow(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(
                        "Account not found for id=" + accountId,
                        AccountErrorCode.ACCOUNT_NOT_FOUND
                ));
    }

    /**
     * Valida que una cuenta pertenezca a un usuario específico.
     *
     * @param account La cuenta a validar
     * @param userId ID del usuario propietario esperado
     * @throws ForbiddenException si la cuenta no pertenece al usuario
     */
    public void validateOwnership(Account account, UserId userId) {
        if (!account.getUserId().equals(userId)) {
            throw new ForbiddenException(
                    "You don't have access to this account",
                    AccountErrorCode.ACCESS_DENIED
            );
        }
    }

    /**
     * Valida que no exista otra cuenta con el mismo nombre para un usuario.
     *
     * @param userId ID del usuario
     * @param name Nombre a validar
     * @throws ValidationException si ya existe una cuenta con ese nombre
     */
    public void validateUniqueName(UserId userId, AccountName name) {
        if (accountRepository.existsByUserIdAndName(userId, name)) {
            throw new ValidationException(
                    "An account with this name already exists",
                    AccountErrorCode.NAME_ALREADY_EXISTS
            );
        }
    }

    /**
     * Valida que no exista otra cuenta con el mismo nombre,
     * excluyendo la cuenta actual (útil para updates).
     *
     * @param currentAccount La cuenta que está siendo actualizada
     * @param newName El nuevo nombre propuesto
     * @param userId ID del usuario
     * @throws ValidationException si ya existe otra cuenta con ese nombre
     */
    public void validateUniqueNameForUpdate(Account currentAccount, AccountName newName, UserId userId) {
        // Si el nombre no cambió, no hay nada que validar
        if (currentAccount.getName().equals(newName)) {
            return;
        }

        validateUniqueName(userId, newName);
    }
}
