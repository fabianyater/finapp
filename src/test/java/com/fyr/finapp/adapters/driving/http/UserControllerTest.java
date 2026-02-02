package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        var createUserRequest = """
                {
                    "name": "John",
                    "surname": "Doe",
                    "email": "john.doe@example.com",
                    "password": "SecurePass123"
                }
                """;
        var userResult = new CreateUserUseCase.UserResult("12345", "john.doe@example.com");
        Mockito.when(createUserUseCase.create(any(CreateUserUseCase.CreateUserCommand.class))).thenReturn(userResult);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("http://localhost/api/v1/users/12345")))
                .andExpect(jsonPath("$.userId", is("12345")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        var invalidRequest = """
                {
                    "name": "",
                    "surname": "Doe",
                    "email": "not-an-email",
                    "password": "short"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldReturnInternalServerErrorWhenUseCaseFails() throws Exception {
        var createUserRequest = """
                {
                    "name": "John",
                    "surname": "Doe",
                    "email": "john.doe@example.com",
                    "password": "SecurePass123"
                }
                """;
        Mockito.when(createUserUseCase.create(any(CreateUserUseCase.CreateUserCommand.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest))
                .andExpect(status().isInternalServerError());
    }
}