package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.transaction.CreateTransferUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class CreateTransferService implements CreateTransferUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateTransferService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final AccountValidator accountValidator;

    public CreateTransferService(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        this.authenticationRepository = authenticationRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    @Transactional
    public Result create(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        log.debug("Creating transfer userId={} from={} to={}", userId.value(), command.fromAccountId(), command.toAccountId());

        var fromId = AccountId.of(command.fromAccountId());
        var toId = AccountId.of(command.toAccountId());

        if (fromId.equals(toId)) {
            throw new ValidationException(
                    "Cannot transfer to the same account",
                    AccountErrorCode.ACCOUNT_NOT_FOUND
            );
        }

        var fromAccount = accountValidator.getAccountAndValidateOwnership(fromId, userId);
        var toAccount = accountValidator.getAccountAndValidateOwnership(toId, userId);

        if (fromAccount.isArchived()) {
            throw new ValidationException("Source account is archived", AccountErrorCode.ACCOUNT_ARCHIVED);
        }
        if (toAccount.isArchived()) {
            throw new ValidationException("Destination account is archived", AccountErrorCode.ACCOUNT_ARCHIVED);
        }

        var amount = Money.of(command.amount(), fromAccount.getCurrency().code());

        if (fromAccount.getCurrentBalance().subtract(amount).isNegative()) {
            throw new ValidationException("Insufficient funds", AccountErrorCode.INSUFFICIENT_FUNDS);
        }

        var occurredOn = Instant.parse(command.occurredOn());
        var description = command.description() != null && !command.description().isBlank()
                ? command.description()
                : "Transferencia";

        var outTransaction = Transaction.create(
                TransactionType.TRANSFER,
                amount,
                description,
                command.note(),
                occurredOn,
                userId,
                null,
                fromId,
                toId,
                null
        );

        var inAmount = Money.of(command.amount(), toAccount.getCurrency().code());
        var inTransaction = Transaction.create(
                TransactionType.TRANSFER,
                inAmount,
                description,
                command.note(),
                occurredOn,
                userId,
                null,
                toId,
                null,
                null
        );

        fromAccount.debit(amount);
        toAccount.credit(inAmount);

        transactionRepository.save(outTransaction);
        transactionRepository.save(inTransaction);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        log.info("Transfer created outTxn={} inTxn={} from={} to={} amount={} userId={}",
                outTransaction.getId().value(), inTransaction.getId().value(),
                fromId.value(), toId.value(), command.amount(), userId.value());

        return new Result(
                outTransaction.getId().value().toString(),
                inTransaction.getId().value().toString()
        );
    }
}
