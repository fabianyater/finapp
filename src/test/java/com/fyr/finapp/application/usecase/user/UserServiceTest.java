package com.fyr.finapp.application.usecase.user;

import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import com.fyr.finapp.domain.exception.ConflictException;
import com.fyr.finapp.domain.model.user.User;
import com.fyr.finapp.domain.model.user.vo.PasswordHash;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.auth.IEncryptionRepository;
import com.fyr.finapp.domain.spi.user.IUserPreferenceRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Test
    void create_shouldSaveUserAndReturnResult_whenDataIsValid() {
        var userRepository = mock(IUserRepository.class);
        var userPreferenceRepository = mock(IUserPreferenceRepository.class);
        var categoryRepository = mock(com.fyr.finapp.domain.spi.category.ICategoryRepository.class);
        var accountRepository = mock(com.fyr.finapp.domain.spi.account.IAccountRepository.class);
        var encryptionRepository = mock(IEncryptionRepository.class);

        var plainPassword = "strongPassword123";
        var hashedPassword = "hashedPassword123hashedPassword123hashedPassword123";
        var userId = new UserId(UUID.randomUUID());
        var email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(encryptionRepository.encode(plainPassword)).thenReturn(new PasswordHash(hashedPassword));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            var user = (User) invocation.getArgument(0);
            user.setId(userId);
            return user;
        });

        var createUserCommand = new CreateUserUseCase.CreateUserCommand("John", "Doe", email, plainPassword);
        var userService = new UserService(userRepository, userPreferenceRepository, accountRepository, encryptionRepository);

        var result = userService.create(createUserCommand);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail().value()).isEqualTo(email);
        assertThat(savedUser.getPasswordHash().value()).isEqualTo(hashedPassword);
        assertThat(savedUser.getUsername().value()).isEqualTo("test");

        verify(userPreferenceRepository).save(argThat(pref -> pref.getUser().equals(userId)));
        assertThat(result.userId()).isEqualTo(userId.value().toString());
        assertThat(result.email()).isEqualTo(email);
    }

    @Test
    void create_shouldThrowEmailAlreadyInUseException_whenEmailExists() {
        var userRepository = mock(IUserRepository.class);
        var userPreferenceRepository = mock(IUserPreferenceRepository.class);
        var categoryRepository = mock(com.fyr.finapp.domain.spi.category.ICategoryRepository.class);
        var accountRepository = mock(com.fyr.finapp.domain.spi.account.IAccountRepository.class);
        var encryptionRepository = mock(IEncryptionRepository.class);

        var email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        var createUserCommand = new CreateUserUseCase.CreateUserCommand("John", "Doe", email, "StrongPassword123");
        var userService = new UserService(userRepository, userPreferenceRepository, accountRepository, encryptionRepository);

        assertThatThrownBy(() -> userService.create(createUserCommand))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository).existsByEmail(email);
        verifyNoInteractions(encryptionRepository);
        verifyNoInteractions(userPreferenceRepository);
    }

    @Test
    void create_shouldSaveUserWithCorrectUsername_whenEmailContainsSpecialCharacters() {
        var userRepository = mock(IUserRepository.class);
        var userPreferenceRepository = mock(IUserPreferenceRepository.class);
        var categoryRepository = mock(com.fyr.finapp.domain.spi.category.ICategoryRepository.class);
        var accountRepository = mock(com.fyr.finapp.domain.spi.account.IAccountRepository.class);
        var encryptionRepository = mock(IEncryptionRepository.class);

        var plainPassword = "anotherStrongPassword";
        var hashedPassword = "anotherHashedPassword";
        var userId = new UserId(UUID.randomUUID());
        var email = "user.namealias@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(encryptionRepository.encode(plainPassword)).thenReturn(new PasswordHash(hashedPassword));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            var user = (User) invocation.getArgument(0);
            user.setId(userId);
            return user;
        });

        var createUserCommand = new CreateUserUseCase.CreateUserCommand("Jane", "Smith", email, plainPassword);
        var userService = new UserService(userRepository, userPreferenceRepository, accountRepository, encryptionRepository);

        var result = userService.create(createUserCommand);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail().value()).isEqualTo(email);
        assertThat(savedUser.getPasswordHash().value()).isEqualTo(hashedPassword);
        assertThat(savedUser.getUsername().value()).isEqualTo("user.namealias");

        verify(userPreferenceRepository).save(argThat(pref -> pref.getUser().equals(userId)));
        assertThat(result.userId()).isEqualTo(userId.value().toString());
        assertThat(result.email()).isEqualTo(email);
    }
}