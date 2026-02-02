package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateUserRequest;
import com.fyr.finapp.adapters.driving.http.dto.CreateUserResponse;
import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/users")
class UserController {
    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(
            @Valid
            @RequestBody
            CreateUserRequest request) {
        var command = new CreateUserUseCase.CreateUserCommand(
                request.name(),
                request.surname(),
                request.email(),
                request.password()
        );

        var result = createUserUseCase.create(command);

        var response = new CreateUserResponse(result.userId(), result.email());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.userId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }
}
