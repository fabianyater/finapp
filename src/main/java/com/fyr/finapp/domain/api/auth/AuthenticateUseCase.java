package com.fyr.finapp.domain.api.auth;

public interface AuthenticateUseCase {
    AuthResult authenticate(LoginCommand command);
    AuthResult refresh(String refreshToken);

    record LoginCommand(String email, String password) {}
    record AuthResult(String accessToken, String refreshToken, String userId, String email) {}
}