package com.fyr.finapp.application.usecase.user;

import com.fyr.finapp.domain.api.user.UserDetailsUseCase;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.messages.UserErrorMessages;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;

public class UserDetailsService implements UserDetailsUseCase {
    private final IUserRepository userRepository;
    private final IAuthenticationRepository authenticationRepository;

    public UserDetailsService(IUserRepository userRepository, IAuthenticationRepository authenticationRepository) {
        this.userRepository = userRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public UserResult get() {
        var userId = authenticationRepository.getCurrentUserId();
        var user = userRepository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException(UserErrorMessages.USER_NOT_FOUND, UserErrorCode.USER_NOT_FOUND);
        }

        return new UserResult(
                user.getId().value().toString(),
                user.getName().value(),
                user.getSurname().value(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
