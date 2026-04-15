package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.api.user.UserDetailsUseCase;

public record UserDetailsResponse(
        String id,
        String name,
        String surname,
        String username,
        String email,
        PreferencesResponse preferences
) {
    public static UserDetailsResponse from(UserDetailsUseCase.UserResult result) {
        var prefs = result.preferences();
        return new UserDetailsResponse(
                result.userId(),
                result.name(),
                result.surname(),
                result.username(),
                result.email(),
                new PreferencesResponse(
                        prefs.locale(),
                        prefs.currency(),
                        prefs.timezone(),
                        prefs.darkMode(),
                        prefs.firstDayOfWeek(),
                        prefs.dateFormat()
                )
        );
    }

    public record PreferencesResponse(
            String locale,
            String currency,
            String timezone,
            Boolean darkMode,
            Short firstDayOfWeek,
            String dateFormat
    ) {}
}
