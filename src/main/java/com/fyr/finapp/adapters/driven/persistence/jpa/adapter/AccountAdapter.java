package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IAccountMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.AccountJpaRepository;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;
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
    public List<Account> findByUserId(UserId userId) {
        return repo.findByUser_Id(userId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndName(UserId userId, AccountName name) {
        return repo.existsByUser_IdAndName(userId.value(), name.value());
    }
}
