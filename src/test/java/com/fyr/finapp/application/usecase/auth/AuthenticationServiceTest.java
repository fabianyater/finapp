package com.fyr.finapp.application.usecase.auth;

import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.model.user.vo.Email;
import com.fyr.finapp.domain.model.user.vo.PlainPassword;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;
    private IAuthenticationRepository authenticationRepository;

    @BeforeEach
    void setup() {
        authenticationRepository = mock(IAuthenticationRepository.class);
        authenticationService = new AuthenticationService(authenticationRepository);
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        String email = "user@example.com";
        String password = "password123";
        AuthenticateUseCase.LoginCommand command = new AuthenticateUseCase.LoginCommand(email, password);
        AuthenticateUseCase.AuthResult expectedResponse = new AuthenticateUseCase.AuthResult("token123", "userId123", email);

        when(authenticationRepository.authenticate(new Email(email), new PlainPassword(password)))
                .thenReturn(expectedResponse);

        AuthenticateUseCase.AuthResult result = authenticationService.authenticate(command);

        assertEquals(expectedResponse, result);
        verify(authenticationRepository, times(1)).authenticate(new Email(email), new PlainPassword(password));
    }

    @Test
    void shouldFailAuthenticationWithInvalidCredentials() {
        String email = "user@example.com";
        String password = "wrongPassword";
        AuthenticateUseCase.LoginCommand command = new AuthenticateUseCase.LoginCommand(email, password);

        when(authenticationRepository.authenticate(new Email(email), new PlainPassword(password)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authenticationService.authenticate(command));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(authenticationRepository, times(1)).authenticate(new Email(email), new PlainPassword(password));
    }
}