package com.fyr.finapp.domain.api.auth;

public interface AuthenticateUseCase {
    AuthResult authenticate(LoginCommand command);

    record LoginCommand(String email, String password) {}
    record AuthResult(String accessToken, String userId, String email) {}
}