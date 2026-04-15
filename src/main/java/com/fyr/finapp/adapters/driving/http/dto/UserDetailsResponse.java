package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.api.user.UserDetailsUseCase;

public record UserDetailsResponse(
        String id,
        String email
) {
    public static UserDetailsResponse from(UserDetailsUseCase.UserResult userDetailsResult) {
        return new UserDetailsResponse(
                userDetailsResult.userId(),
                userDetailsResult.email()
        );
    }
}
