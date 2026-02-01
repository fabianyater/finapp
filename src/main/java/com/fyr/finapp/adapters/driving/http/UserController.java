package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateUserRequest;
import com.fyr.finapp.adapters.driving.http.dto.CreateUserResponse;
import com.fyr.finapp.domain.api.user.CreateUserUseCase;
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
@RequestMapping("/api/v1/users")
class UserController {
    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var command = new CreateUserUseCase.CreateUserCommand(
                request.name(),
                request.surname(),
                request.email(),
                request.password()
        );

        var result = createUserUseCase.create(command);

        var response = new CreateUserResponse(result.userId(), result.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
