package com.fyr.finapp.application.usecase.user;

import com.fyr.finapp.domain.api.user.UserDetailsUseCase;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.messages.UserErrorMessages;
import com.fyr.finapp.domain.model.user.UserPreference;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.user.IUserPreferenceRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;

public class UserDetailsService implements UserDetailsUseCase {
    private final IUserRepository userRepository;
    private final IUserPreferenceRepository userPreferenceRepository;
    private final IAuthenticationRepository authenticationRepository;

    public UserDetailsService(IUserRepository userRepository, IUserPreferenceRepository userPreferenceRepository, IAuthenticationRepository authenticationRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public UserResult get() {
        var userId = authenticationRepository.getCurrentUserId();
        var user = userRepository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException(UserErrorMessages.USER_NOT_FOUND, UserErrorCode.USER_NOT_FOUND);
        }

        var preferences = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserPreference.defaultFor(userId));

        return new UserResult(
                user.getId().value().toString(),
                user.getName().value(),
                user.getSurname().value(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                new PreferenceResult(
                        preferences.getLocale().value(),
                        preferences.getCurrency().code(),
                        preferences.getTimezone().value(),
                        preferences.getTheme(),
                        preferences.getFirstDayOfWeek().value(),
                        preferences.getDateFormat().value()
                )
        );
    }
}
