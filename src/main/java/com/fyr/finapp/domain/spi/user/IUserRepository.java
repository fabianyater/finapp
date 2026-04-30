package com.fyr.finapp.domain.spi.user;

import com.fyr.finapp.domain.model.user.User;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.util.Optional;

public interface IUserRepository {
    User save(User user);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    User getUserById(UserId id);
    void delete(UserId id);
}
