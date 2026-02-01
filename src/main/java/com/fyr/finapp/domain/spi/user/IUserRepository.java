package com.fyr.finapp.domain.spi.user;

import com.fyr.finapp.domain.model.user.User;

public interface IUserRepository {
    User save(User user);
    boolean existsByEmail(String email);
}
