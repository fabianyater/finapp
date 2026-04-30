package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IUserEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserJpaRepository;
import com.fyr.finapp.domain.model.user.User;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.user.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserAdapter implements IUserRepository {
    private final UserJpaRepository userJpaRepository;
    private final IUserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        if (user.getId() != null) {
            var existing = userJpaRepository.findById(user.getId().value());
            if (existing.isPresent()) {
                var entity = existing.get();
                entity.setName(user.getName().value());
                entity.setSurname(user.getSurname().value());
                entity.setEmail(user.getEmail().value());
                entity.setUsername(user.getUsername().value());
                if (user.getPasswordHash() != null) entity.setPasswordHash(user.getPasswordHash().value());
                return userEntityMapper.toUser(userJpaRepository.save(entity));
            }
        }

        return userEntityMapper.toUser(userJpaRepository.save(userEntityMapper.toEntity(user)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userEntityMapper::toUser);
    }

    @Override
    public User getUserById(UserId id) {
        return userJpaRepository.findById(id.value())
                .map(userEntityMapper::toUser)
                .orElse(null);
    }

    @Override
    public void delete(UserId id) {
        userJpaRepository.deleteById(id.value());
    }
}
