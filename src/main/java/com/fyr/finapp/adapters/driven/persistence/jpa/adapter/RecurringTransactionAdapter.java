package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.CategoryEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.RecurringTransactionEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IRecurringTransactionMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.RecurringTransactionJpaRepository;
import com.fyr.finapp.domain.model.recurring.RecurringTransaction;
import com.fyr.finapp.domain.model.recurring.RecurringTransactionId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class RecurringTransactionAdapter implements IRecurringTransactionRepository {
    private final IRecurringTransactionMapper mapper;
    private final RecurringTransactionJpaRepository jpaRepository;
    private final EntityManager entityManager;

    @Override
    public void save(RecurringTransaction rt) {
        UUID id = rt.getId().value();
        RecurringTransactionEntity entity = entityManager.find(RecurringTransactionEntity.class, id);

        if (entity == null) {
            entity = new RecurringTransactionEntity();
            entity.setId(id);
        }

        mapper.updateEntityFromDomain(rt, entity);
        entity.setUser(entityManager.getReference(UserEntity.class, rt.getUserId().value()));
        entity.setAccount(entityManager.getReference(AccountEntity.class, rt.getAccountId().value()));
        entity.setToAccount(rt.getToAccountId() != null
                ? entityManager.getReference(AccountEntity.class, rt.getToAccountId().value())
                : null);
        entity.setCategory(rt.getCategoryId() != null
                ? entityManager.getReference(CategoryEntity.class, rt.getCategoryId().value())
                : null);

        jpaRepository.save(entity);
    }

    @Override
    public Optional<RecurringTransaction> findById(RecurringTransactionId id, UserId userId) {
        return jpaRepository.findByIdAndUser_Id(id.value(), userId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<RecurringTransaction> findAllByUserId(UserId userId) {
        return jpaRepository.findAllByUser_Id(userId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RecurringTransaction> findDue(LocalDate today) {
        return jpaRepository.findDue(today)
                .stream().map(mapper::toDomain).toList();
    }
}
