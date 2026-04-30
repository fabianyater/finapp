package com.fyr.finapp.application.usecase.user;

import com.fyr.finapp.domain.api.user.UpdateProfileUseCase;
import com.fyr.finapp.domain.api.user.UserDetailsUseCase;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.messages.UserErrorMessages;
import com.fyr.finapp.domain.model.user.UserPreference;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;
import com.fyr.finapp.domain.model.user.vo.PersonName;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.user.IUserPreferenceRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;

public class UpdateProfileService implements UpdateProfileUseCase {

    private final IUserRepository userRepository;
    private final IUserPreferenceRepository userPreferenceRepository;
    private final IAuthenticationRepository authenticationRepository;

    public UpdateProfileService(IUserRepository userRepository,
                                IUserPreferenceRepository userPreferenceRepository,
                                IAuthenticationRepository authenticationRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public UserDetailsUseCase.UserResult update(UpdateProfileCommand command) {
        var userId = authenticationRepository.getCurrentUserId();
        var user = userRepository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException(UserErrorMessages.USER_NOT_FOUND, UserErrorCode.USER_NOT_FOUND);
        }

        if (command.name() != null) user.setName(new PersonName(command.name()));
        if (command.surname() != null) user.setSurname(new PersonName(command.surname()));

        var updated = userRepository.save(user);
        var prefs = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserPreference.defaultFor(userId));

        return new UserDetailsUseCase.UserResult(
                updated.getId().value().toString(),
                updated.getName().value(),
                updated.getSurname().value(),
                updated.getUsername().value(),
                updated.getEmail().value(),
                updated.getCreatedAt(),
                updated.getUpdatedAt(),
                new UserDetailsUseCase.PreferenceResult(
                        prefs.getLocale().value(),
                        prefs.getCurrency().code(),
                        prefs.getTimezone().value(),
                        prefs.getTheme(),
                        prefs.getFirstDayOfWeek().value(),
                        prefs.getDateFormat().value()
                )
        );
    }
}
