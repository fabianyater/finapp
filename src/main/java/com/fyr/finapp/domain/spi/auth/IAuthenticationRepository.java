package com.fyr.finapp.domain.spi.auth;

import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.model.user.vo.Email;
import com.fyr.finapp.domain.model.user.vo.PlainPassword;
import com.fyr.finapp.domain.model.user.vo.UserId;

public interface IAuthenticationRepository {
    AuthenticateUseCase.AuthResult authenticate(Email email, PlainPassword password);
    UserId getCurrentUserId();
}
