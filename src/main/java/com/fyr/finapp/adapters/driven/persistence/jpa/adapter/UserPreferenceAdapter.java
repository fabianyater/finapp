package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserPreferenceEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IUserPreferenceEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserPreferenceJpaRepository;
import com.fyr.finapp.domain.model.user.UserPreference;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.user.IUserPreferenceRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserPreferenceAdapter implements IUserPreferenceRepository {
    private final UserPreferenceJpaRepository repo;
    private final IUserPreferenceEntityMapper mapper;
    private final EntityManager entityManager;

    @Override
    public void save(UserPreference userPreference) {
        UUID userId = userPreference.getUser().value();
        UserPreferenceEntity existing = entityManager.find(UserPreferenceEntity.class, userId);
        if (existing != null) {
            mapper.updateEntity(userPreference, existing);
        } else {
            UserPreferenceEntity entity = mapper.toEntity(userPreference);
            entity.setUser(entityManager.getReference(UserEntity.class, userId));
            entityManager.persist(entity);
        }
    }

    @Override
    public Optional<UserPreference> findByUserId(UserId userId) {
        return repo.findById(userId.value())
                .map(mapper::toUser);
    }
}
