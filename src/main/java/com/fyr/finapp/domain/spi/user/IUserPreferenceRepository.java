package com.fyr.finapp.domain.spi.user;

import com.fyr.finapp.domain.model.user.UserPreference;

public interface IUserPreferenceRepository {
    void save(UserPreference userPreference);
}
