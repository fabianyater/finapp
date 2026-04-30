package com.fyr.finapp.application.usecase.auth;

import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.model.user.vo.Email;
import com.fyr.finapp.domain.model.user.vo.PlainPassword;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;

public class AuthenticationService implements AuthenticateUseCase {
    private final IAuthenticationRepository authenticationRepository;

    public AuthenticationService(IAuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public AuthResult authenticate(LoginCommand command) {
        Email email = new Email(command.email());
        PlainPassword password = new PlainPassword(command.password());

        return authenticationRepository.authenticate(email, password);
    }

    @Override
    public AuthResult refresh(String refreshToken) {
        return authenticationRepository.refreshToken(refreshToken);
    }
}
