package com.fyr.finapp.application.usecase.user;

import com.fyr.finapp.domain.api.user.DeleteUserUseCase;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;

public class DeleteUserService implements DeleteUserUseCase {
    private final IUserRepository userRepository;
    private final IAuthenticationRepository authenticationRepository;

    public DeleteUserService(IUserRepository userRepository, IAuthenticationRepository authenticationRepository) {
        this.userRepository = userRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public void delete() {
        var userId = authenticationRepository.getCurrentUserId();
        userRepository.delete(userId);
    }
}
