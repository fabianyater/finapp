package com.fyr.finapp.domain.spi.user;

import com.fyr.finapp.domain.model.user.User;
import com.fyr.finapp.domain.model.user.vo.UserId;

public interface IUserRepository {
    User save(User user);
    boolean existsByEmail(String email);
    User getUserById(UserId id);
}
