package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.CategoryEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.TransactionEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.ITransactionMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.TransactionJpaRepository;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.transaction.TransactionId;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TransactionAdapter implements ITransactionRepository {
    private final ITransactionMapper transactionEntityMapper;
    private final TransactionJpaRepository transactionJpaRepository;
    private final EntityManager entityManager;

    @Override
    public void save(Transaction transaction) {
        UUID id = transaction.getId().value();
        Optional<TransactionEntity> existing = transactionJpaRepository.findById(id);
        TransactionEntity entity;

        if (existing.isPresent()) {
            entity = existing.get();
            transactionEntityMapper.updateEntityFromDomain(transaction, entity);
        } else {
            entity = new TransactionEntity();
            entity.setId(id);
            transactionEntityMapper.updateEntityFromDomain(transaction, entity);
        }

        entity.setUser(entityManager.getReference(UserEntity.class, transaction.getUserId().value()));
        entity.setCategories(entityManager.getReference(CategoryEntity.class, transaction.getCategoryId().value()));
        entity.setAccounts(entityManager.getReference(AccountEntity.class, transaction.getAccountId().value()));

        transactionJpaRepository.save(entity);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return transactionJpaRepository.findById(id.value())
                .map(transactionEntityMapper::toDomain);
    }
}
