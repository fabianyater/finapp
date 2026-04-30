package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountMemberEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IAccountMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.AccountJpaRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.AccountMemberJpaRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.specification.AccountSpecifications;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountAdapter implements IAccountRepository {
    private final AccountJpaRepository repo;
    private final AccountMemberJpaRepository memberRepo;
    private final EntityManager entityManager;
    private final IAccountMapper mapper;

    @Override
    public void save(Account account) {
        UUID id = account.getId().value();
        Optional<AccountEntity> existing = repo.findById(id);
        AccountEntity entity;

        if (existing.isPresent()) {
            entity = existing.get();
            mapper.updateEntityFromDomain(account, entity);
        } else {
            entity = new AccountEntity();
            entity.setId(id);
            mapper.updateEntityFromDomain(account, entity);
        }

        entity.setUser(entityManager.getReference(UserEntity.class, account.getUserId().value()));

        repo.save(entity);
    }

    @Override
    public PagedAccounts findByUserId(UserId userId, AccountFilters filters) {
        Sort.Direction direction = filters.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, mapSortField(filters.sortBy()));

        var pageable = PageRequest.of(filters.page(), filters.size(), sort);
        var spec = generateAccountFilterSpec(userId, filters);
        var pageResult = repo.findAll(spec, pageable);
        var accounts = pageResult.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new PagedAccounts(
                accounts,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext(),
                pageResult.hasPrevious()
        );
    }

    @Override
    public List<Account> findAllByUserId(UserId userId) {
        return repo.findAllAccessibleByUserId(userId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void addMember(AccountId accountId, UserId userId, UserId invitedBy) {
        var member = new AccountMemberEntity();
        member.setAccountId(accountId.value());
        member.setUserId(userId.value());
        member.setInvitedBy(invitedBy.value());
        member.setJoinedAt(OffsetDateTime.now());
        memberRepo.save(member);
    }

    @Override
    public void removeMember(AccountId accountId, UserId userId) {
        memberRepo.deleteByAccountIdAndUserId(accountId.value(), userId.value());
    }

    @Override
    public List<MemberInfo> findMembers(AccountId accountId) {
        return memberRepo.findMembersWithUsers(accountId.value())
                .stream()
                .map(m -> new MemberInfo(
                        m.getUserId().toString(),
                        m.getUser().getEmail(),
                        m.getUser().getName(),
                        m.getJoinedAt().toInstant()
                ))
                .toList();
    }

    @Override
    public boolean isMember(AccountId accountId, UserId userId) {
        return memberRepo.existsByAccountIdAndUserId(accountId.value(), userId.value());
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return repo.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndName(UserId userId, AccountName name) {
        return repo.existsByUser_IdAndNameAllIgnoreCase(userId.value(), name.value());
    }

    @Override
    public int unmarkAllAsDefault(UserId userId) {
        return repo.unmarkAllAsDefault(userId.value());
    }

    @Override
    public void delete(AccountId id) {
        repo.deleteById(id.value());
    }

    private String mapSortField(String domainField) {
        return switch (domainField) {
            case "initialBalance" -> "initialBalance";
            case "name" -> "name";
            case "type" -> "type";
            default -> domainField;
        };
    }

    private static @NonNull Specification<AccountEntity> generateAccountFilterSpec(UserId userId, AccountFilters filters) {
        return AccountSpecifications.withFilters(
                userId.value(),
                filters.types(),
                filters.search(),
                filters.createdAfter(),
                filters.createdBefore()
        );
    }
}
