package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.CategoryEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.ITransactionMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.TransactionJpaRepository;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class TransactionAdapter implements ITransactionRepository {
    private final ITransactionMapper transactionEntityMapper;
    private final TransactionJpaRepository transactionJpaRepository;
    private final EntityManager entityManager;

    @Override
    public void save(Transaction transaction) {
        UUID id = transaction.getId().value();

        var entity = transactionEntityMapper.toEntity(transaction);

        entity.setUser(entityManager.getReference(UserEntity.class, transaction.getUserId().value()));
        entity.setCategories(entityManager.getReference(CategoryEntity.class, transaction.getCategoryId().value()));
        entity.setAccounts(entityManager.getReference(AccountEntity.class, transaction.getAccountId().value()));

        entity.setId(id);

        transactionJpaRepository.save(entity);
    }
}
