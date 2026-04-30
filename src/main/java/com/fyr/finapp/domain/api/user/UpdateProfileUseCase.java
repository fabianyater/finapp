package com.fyr.finapp.domain.api.user;

public interface UpdateProfileUseCase {
    UserDetailsUseCase.UserResult update(UpdateProfileCommand command);

    record UpdateProfileCommand(String name, String surname) {}
}
