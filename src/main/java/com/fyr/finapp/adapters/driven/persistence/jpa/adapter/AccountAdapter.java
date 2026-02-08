package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IAccountMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.AccountJpaRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.specification.AccountSpecifications;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@RequiredArgsConstructor
public class AccountAdapter implements IAccountRepository {
    private final AccountJpaRepository repo;
    private final EntityManager entityManager;
    private final IAccountMapper mapper;

    @Override
    public void save(Account account) {
        var accountEntity = mapper.toEntity(account);
        UUID userId = account.getUserId().value();

        accountEntity.setUser(entityManager.getReference(
                UserEntity.class,
                userId
        ));


        repo.save(accountEntity);
    }

    @Override
    public PagedAccounts findByUserId(UserId userId, AccountFilters filters) {
        Sort.Direction direction = filters.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, mapSortField(filters.sortBy()));

        var pageable = PageRequest.of(filters.page(), filters.size(), sort);
        var spec = generateAccountFilterSpec(userId, filters);
        var pageResult = repo.findAll(spec, pageable);
        var accounts = pageResult.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new PagedAccounts(accounts, pageResult.getTotalElements());
    }

    @Override
    public boolean existsByUserIdAndName(UserId userId, AccountName name) {
        return repo.existsByUser_IdAndName(userId.value(), name.value());
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
