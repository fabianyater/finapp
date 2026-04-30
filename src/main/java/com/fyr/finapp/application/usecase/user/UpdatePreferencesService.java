package com.fyr.finapp.application.usecase.user;

import com.fyr.finapp.domain.api.user.UpdatePreferencesUseCase;
import com.fyr.finapp.domain.api.user.UserDetailsUseCase;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.messages.UserErrorMessages;
import com.fyr.finapp.domain.model.user.UserPreference;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;
import com.fyr.finapp.domain.model.user.vo.DateFormatPattern;
import com.fyr.finapp.domain.model.user.vo.LocaleTag;
import com.fyr.finapp.domain.shared.vo.Currency;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.user.IUserPreferenceRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;

public class UpdatePreferencesService implements UpdatePreferencesUseCase {
    private final IUserRepository userRepository;
    private final IUserPreferenceRepository userPreferenceRepository;
    private final IAuthenticationRepository authenticationRepository;

    public UpdatePreferencesService(IUserRepository userRepository,
                                    IUserPreferenceRepository userPreferenceRepository,
                                    IAuthenticationRepository authenticationRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public UserDetailsUseCase.UserResult update(UpdatePreferencesCommand command) {
        var userId = authenticationRepository.getCurrentUserId();
        var user = userRepository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException(UserErrorMessages.USER_NOT_FOUND, UserErrorCode.USER_NOT_FOUND);
        }

        var prefs = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserPreference.defaultFor(userId));

        if (command.currency() != null) prefs.setCurrency(new Currency(command.currency()));
        if (command.language() != null) prefs.setLocale(new LocaleTag(command.language()));
        if (command.dateFormat() != null) prefs.setDateFormat(new DateFormatPattern(command.dateFormat()));
        if (command.theme() != null) prefs.setTheme(command.theme());

        userPreferenceRepository.save(prefs);

        return new UserDetailsUseCase.UserResult(
                user.getId().value().toString(),
                user.getName().value(),
                user.getSurname().value(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
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
