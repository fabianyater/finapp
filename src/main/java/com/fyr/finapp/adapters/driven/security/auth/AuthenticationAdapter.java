package com.fyr.finapp.adapters.driven.security.auth;

import com.fyr.finapp.adapters.driven.security.jwt.JwtProvider;
import com.fyr.finapp.adapters.driven.security.user.SecurityUser;
import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.model.user.vo.Email;
import com.fyr.finapp.domain.model.user.vo.PlainPassword;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Objects;

@RequiredArgsConstructor
public class AuthenticationAdapter implements IAuthenticationRepository {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public AuthenticateUseCase.AuthResult authenticate(Email email, PlainPassword password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email.value(), password.value())
        );

        SecurityUser principal = (SecurityUser) auth.getPrincipal();
        String token = jwtProvider.generateToken(Objects.requireNonNull(principal));

        return new AuthenticateUseCase.AuthResult(token, principal.getId().toString(), principal.getUsername());
    }
}
