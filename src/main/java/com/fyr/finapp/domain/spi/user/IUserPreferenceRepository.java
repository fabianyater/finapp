package com.fyr.finapp.domain.spi.user;

import com.fyr.finapp.domain.model.user.UserPreference;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.util.Optional;

public interface IUserPreferenceRepository {
    void save(UserPreference userPreference);
    Optional<UserPreference> findByUserId(UserId userId);
}
