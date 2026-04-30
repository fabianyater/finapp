package com.fyr.finapp.domain.api.user;

public interface UpdatePreferencesUseCase {
    UserDetailsUseCase.UserResult update(UpdatePreferencesCommand command);

    record UpdatePreferencesCommand(String currency, String language, String dateFormat, String theme) {}
}
