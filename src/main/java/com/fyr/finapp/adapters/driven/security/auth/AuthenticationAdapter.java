package com.fyr.finapp.adapters.driven.security.auth;

import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserJpaRepository;
import com.fyr.finapp.adapters.driven.security.jwt.JwtProperties;
import com.fyr.finapp.adapters.driven.security.jwt.JwtProvider;
import com.fyr.finapp.adapters.driven.security.user.SecurityUser;
import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.model.user.vo.Email;
import com.fyr.finapp.domain.model.user.vo.PlainPassword;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthenticationAdapter implements IAuthenticationRepository {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final UserJpaRepository userJpaRepository;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthenticateUseCase.AuthResult authenticate(Email email, PlainPassword password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email.value(), password.value())
        );

        SecurityUser principal = (SecurityUser) auth.getPrincipal();
        String accessToken = jwtProvider.generateToken(Objects.requireNonNull(principal));
        String refreshToken = generateAndStoreRefreshToken(principal.getId().toString());

        return new AuthenticateUseCase.AuthResult(accessToken, refreshToken, principal.getId().toString(), principal.getUsername());
    }

    @Override
    public AuthenticateUseCase.AuthResult refreshToken(String refreshToken) {
        var userEntity = userJpaRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (userEntity.getRefreshTokenExpiresAt() == null ||
                userEntity.getRefreshTokenExpiresAt().isBefore(OffsetDateTime.now())) {
            userEntity.setRefreshToken(null);
            userEntity.setRefreshTokenExpiresAt(null);
            userJpaRepository.save(userEntity);
            throw new IllegalArgumentException("Refresh token expired");
        }

        SecurityUser principal = (SecurityUser) userDetailsService.loadUserByUsername(userEntity.getEmail());
        String newAccessToken = jwtProvider.generateToken(principal);
        String newRefreshToken = generateAndStoreRefreshToken(userEntity.getId().toString());

        return new AuthenticateUseCase.AuthResult(newAccessToken, newRefreshToken, userEntity.getId().toString(), userEntity.getEmail());
    }

    @Override
    public UserId getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        SecurityUser userDetails = Objects.requireNonNull((SecurityUser) authentication.getPrincipal());
        var id = Objects.requireNonNull(userDetails.getId());

        return UserId.of(id.toString());
    }

    private String generateAndStoreRefreshToken(String userId) {
        var userEntity = userJpaRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String token = UUID.randomUUID().toString();
        userEntity.setRefreshToken(token);
        userEntity.setRefreshTokenExpiresAt(
                OffsetDateTime.now().plusSeconds(jwtProperties.getRefreshExpirationInSeconds())
        );
        userJpaRepository.save(userEntity);
        return token;
    }
}
