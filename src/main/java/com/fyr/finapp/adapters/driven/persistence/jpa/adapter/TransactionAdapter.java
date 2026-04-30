package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.dto.CategorySummaryDto;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountMemberEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.CategoryEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.TransactionEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.ITransactionMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.TransactionJpaRepository;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.transaction.TransactionId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.pagination.PageRequest;
import com.fyr.finapp.domain.shared.pagination.SortDirection;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class TransactionAdapter implements ITransactionRepository {
    private final ITransactionMapper transactionEntityMapper;
    private final TransactionJpaRepository transactionJpaRepository;
    private final EntityManager entityManager;

    @Override
    public void save(Transaction transaction) {
        UUID id = transaction.getId().value();
        // Use EntityManager.find() to bypass @SQLRestriction — findById() filters deleted rows
        TransactionEntity existing = entityManager.find(TransactionEntity.class, id);
        TransactionEntity entity;

        if (existing != null) {
            entity = existing;
            transactionEntityMapper.updateEntityFromDomain(transaction, entity);
        } else {
            entity = new TransactionEntity();
            entity.setId(id);
            transactionEntityMapper.updateEntityFromDomain(transaction, entity);
        }

        entity.setUser(entityManager.getReference(UserEntity.class, transaction.getUserId().value()));
        entity.setAccounts(entityManager.getReference(AccountEntity.class, transaction.getAccountId().value()));
        if (transaction.getCategoryId() != null) {
            entity.setCategories(entityManager.getReference(CategoryEntity.class, transaction.getCategoryId().value()));
        } else {
            entity.setCategories(null);
        }
        if (transaction.getToAccountId() != null) {
            entity.setToAccount(entityManager.getReference(AccountEntity.class, transaction.getToAccountId().value()));
        } else {
            entity.setToAccount(null);
        }

        transactionJpaRepository.save(entity);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return transactionJpaRepository.findById(id.value())
                .map(transactionEntityMapper::toDomain);
    }

    @Override
    public PagedTransactions findByUserId(UserId userId, TransactionFilters filters) {
        var spec = buildSpec(userId, filters);
        var pageable = buildPageable(filters.pageRequest());
        var page = transactionJpaRepository.findAll(spec, pageable);

        return new PagedTransactions(
                page.getContent().stream().map(transactionEntityMapper::toDomain).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Override
    public Optional<Transaction> getTransactionByIdAndAccountId(TransactionId transactionId, AccountId id) {
        return transactionJpaRepository.findByIdAndAccounts_Id(transactionId.value(), id.value())
                .map(transactionEntityMapper::toDomain);
    }

    @Override
    public List<CategorySummaryEntry> findCategorySummary(UserId userId, String accountId, String type, java.time.Instant dateFrom, java.time.Instant dateTo) {
        java.time.OffsetDateTime from = (dateFrom != null ? dateFrom : java.time.Instant.EPOCH)
                .atOffset(ZoneOffset.UTC);
        java.time.OffsetDateTime to = (dateTo != null ? dateTo : java.time.Instant.now().plusSeconds(86400))
                .atOffset(ZoneOffset.UTC);
        return transactionJpaRepository
                .findCategorySummary(userId.value(), UUID.fromString(accountId), type, from, to)
                .stream()
                .map(dto -> new CategorySummaryEntry(
                        dto.categoryId().toString(),
                        dto.name(),
                        dto.color(),
                        dto.icon(),
                        dto.total()
                ))
                .toList();
    }

    @Override
    public List<Transaction> findDeletedByAccountId(AccountId accountId, UserId userId) {
        return transactionJpaRepository.findDeletedByUserIdAndAccountId(userId.value(), accountId.value())
                .stream()
                .map(transactionEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Transaction> findDeletedByIdAndAccountId(TransactionId id, AccountId accountId) {
        return transactionJpaRepository.findDeletedByIdAndAccountId(id.value(), accountId.value())
                .map(transactionEntityMapper::toDomain);
    }

    @Override
    public Optional<Transaction> findPairedTransfer(TransactionId excludeId, AccountId pairedAccountId, Instant occurredOn, Long amount, UserId userId) {
        return transactionJpaRepository.findPairedTransfer(
                userId.value(),
                excludeId.value(),
                pairedAccountId.value(),
                occurredOn.atOffset(java.time.ZoneOffset.UTC),
                amount
        ).map(transactionEntityMapper::toDomain);
    }

    @Override
    public List<Transaction> findAllByUserId(UserId userId, TransactionFilters filters) {
        var spec = buildSpec(userId, filters);
        return transactionJpaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "occurredOn"))
                .stream().map(transactionEntityMapper::toDomain).toList();
    }

    private Specification<TransactionEntity> buildSpec(UserId userId, TransactionFilters filters) {
        Specification<TransactionEntity> spec = Specification.where(hasUserId(userId));

        if (!filters.accountIds().isEmpty())
            spec = spec.and(hasAccountIds(filters.accountIds()));
        if (!filters.categoryIds().isEmpty())
            spec = spec.and(hasCategoryIds(filters.categoryIds()));
        if (!filters.types().isEmpty())
            spec = spec.and(hasTypes(filters.types()));
        if (filters.search() != null && !filters.search().isBlank())
            spec = spec.and(hasSearch(filters.search()));
        if (filters.dateFrom() != null)
            spec = spec.and(hasDateFrom(filters.dateFrom()));
        if (filters.dateTo() != null)
            spec = spec.and(hasDateTo(filters.dateTo()));
        if (!filters.tags().isEmpty())
            spec = spec.and(hasTagsFilter(filters.tags()));

        return spec;
    }

    private Specification<TransactionEntity> hasUserId(UserId userId) {
        return (root, query, cb) -> {
            // Account owned by this user
            Predicate isAccountOwner = cb.equal(root.get("accounts").get("user").get("id"), userId.value());

            // User is a member of the account
            Subquery<UUID> memberSub = query.subquery(UUID.class);
            Root<AccountMemberEntity> memberRoot = memberSub.from(AccountMemberEntity.class);
            memberSub.select(memberRoot.get("accountId"))
                    .where(cb.equal(memberRoot.get("userId"), userId.value()));
            Predicate isMember = root.get("accounts").get("id").in(memberSub);

            return cb.or(isAccountOwner, isMember);
        };
    }

    private Specification<TransactionEntity> hasAccountIds(Set<String> accountIds) {
        if (accountIds == null || accountIds.isEmpty()) return null;
        return (root, query, cb) ->
                root.get("accounts").get("id").in(
                        accountIds.stream().map(UUID::fromString).toList()
                );
    }

    private Specification<TransactionEntity> hasCategoryIds(Set<String> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return null;
        return (root, query, cb) ->
                root.get("categories").get("id").in(
                        categoryIds.stream().map(UUID::fromString).toList()
                );
    }

    private Specification<TransactionEntity> hasTypes(Set<String> types) {
        if (types == null || types.isEmpty()) return null;
        return (root, query, cb) ->
                root.get("type").in(types);
    }

    private Specification<TransactionEntity> hasSearch(String search) {
        if (search == null || search.isBlank()) return null;
        return (root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("note")), "%" + search.toLowerCase() + "%")
                );
    }

    private Specification<TransactionEntity> hasDateFrom(Instant dateFrom) {
        if (dateFrom == null) return null;
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("occurredOn"), dateFrom.atOffset(ZoneOffset.UTC));
    }

    private Specification<TransactionEntity> hasDateTo(Instant dateTo) {
        if (dateTo == null) return null;
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("occurredOn"), dateTo.atOffset(ZoneOffset.UTC));
    }

    private Specification<TransactionEntity> hasTagsFilter(Set<String> tags) {
        if (tags == null || tags.isEmpty()) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            var tagsJoin = root.join("tags");
            return tagsJoin.in(tags);
        };
    }

    @Override
    public List<String> findAllTagsByUserId(UserId userId) {
        return transactionJpaRepository.findAllTagsByUserId(userId.value());
    }

    @Override
    public void renameTag(UserId userId, String oldTag, String newTag) {
        transactionJpaRepository.renameTag(userId.value(), oldTag, newTag);
    }

    @Override
    public void deleteTag(UserId userId, String tag) {
        transactionJpaRepository.deleteTag(userId.value(), tag);
    }

    private Pageable buildPageable(PageRequest pageRequest) {
        var direction = pageRequest.direction() == SortDirection.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.page(),
                pageRequest.size(),
                Sort.by(direction, pageRequest.sortBy())
        );
    }
}
