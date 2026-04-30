package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.BudgetEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.BudgetJpaRepository;
import com.fyr.finapp.domain.model.budget.Budget;
import com.fyr.finapp.domain.model.budget.BudgetId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.budget.IBudgetRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class BudgetAdapter implements IBudgetRepository {
    private final BudgetJpaRepository jpaRepository;

    @Override
    public void save(Budget budget) {
        var entity = jpaRepository.findById(budget.getId().value())
                .orElseGet(BudgetEntity::new);
        entity.setId(budget.getId().value());
        entity.setUserId(budget.getUserId().value());
        entity.setCategoryId(budget.getCategoryId().value());
        entity.setLimitAmount(budget.getLimitAmount());
        entity.setCreatedAt(budget.getCreatedAt().atOffset(ZoneOffset.UTC));
        entity.setUpdatedAt(budget.getUpdatedAt().atOffset(ZoneOffset.UTC));
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Budget> findById(BudgetId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Budget> findByCategoryIdAndUserId(CategoryId categoryId, UserId userId) {
        return jpaRepository.findByCategoryIdAndUserId(categoryId.value(), userId.value())
                .map(this::toDomain);
    }

    @Override
    public List<BudgetWithSpent> findAllWithSpent(UserId userId, Instant periodStart, Instant periodEnd) {
        var rows = jpaRepository.findAllWithSpent(
                userId.value(),
                periodStart.atOffset(ZoneOffset.UTC),
                periodEnd.atOffset(ZoneOffset.UTC)
        );
        return rows.stream().map(row -> new BudgetWithSpent(
                BudgetId.of(row[0].toString()),
                CategoryId.of(row[1].toString()),
                (String) row[3],
                (String) row[4],
                (String) row[5],
                ((Number) row[2]).longValue(),
                ((Number) row[6]).longValue()
        )).toList();
    }

    @Override
    public void delete(BudgetId id) {
        jpaRepository.deleteById(id.value());
    }

    private Budget toDomain(BudgetEntity e) {
        return Budget.reconstruct(
                BudgetId.of(e.getId().toString()),
                UserId.of(e.getUserId().toString()),
                CategoryId.of(e.getCategoryId().toString()),
                e.getLimitAmount(),
                e.getCreatedAt().toInstant(),
                e.getUpdatedAt().toInstant()
        );
    }
}
