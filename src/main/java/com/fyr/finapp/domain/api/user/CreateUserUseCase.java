package com.fyr.finapp.domain.api.user;

public interface CreateUserUseCase {
    UserResult create(CreateUserCommand command);

    record CreateUserCommand(
            String name,
            String surname,
            String email,
            String password
    ) {}

    record UserResult(
            String userId,
            String email
    ) {}
}
