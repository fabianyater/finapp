package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
class AuthController {
    private final AuthenticateUseCase authenticateUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthenticateUseCase.AuthResult> login(
            @Valid @RequestBody AuthenticateUseCase.LoginCommand request
    ) {
        var result = authenticateUseCase.authenticate(request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
