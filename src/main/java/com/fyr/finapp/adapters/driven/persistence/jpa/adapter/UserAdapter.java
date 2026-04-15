package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IUserEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserJpaRepository;
import com.fyr.finapp.domain.model.user.User;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.user.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class UserAdapter implements IUserRepository {
    private final UserJpaRepository userJpaRepository;
    private final IUserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        var userEntity = userEntityMapper.toEntity(user);

        return userEntityMapper.toUser(userJpaRepository.save(userEntity));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User getUserById(UserId id) {
        return userJpaRepository.findById(UUID.fromString(id.toString()))
                .map(userEntityMapper::toUser)
                .orElse(null);
    }
}
