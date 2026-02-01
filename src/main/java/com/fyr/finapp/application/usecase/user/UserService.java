package com.fyr.finapp.application.usecase.user;

import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.EmailAlreadyInUseException;
import com.fyr.finapp.domain.exception.messages.UserErrorMessages;
import com.fyr.finapp.domain.model.user.User;
import com.fyr.finapp.domain.model.user.UserPreference;
import com.fyr.finapp.domain.model.user.vo.*;
import com.fyr.finapp.domain.spi.auth.IEncryptionRepository;
import com.fyr.finapp.domain.spi.user.IUserPreferenceRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;

public class UserService implements CreateUserUseCase {
    public static final String AT_SYMBOL = "@";
    private final IUserRepository userRepository;
    private final IUserPreferenceRepository userPreferenceRepository;
    private final IEncryptionRepository encryptionRepository;

    public UserService(IUserRepository userRepository,
                       IUserPreferenceRepository userPreferenceRepository,
                       IEncryptionRepository encryptionRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.encryptionRepository = encryptionRepository;
    }


    @Override
    public UserResult create(CreateUserCommand command) {
        Email email = new Email(command.email());

        if (userRepository.existsByEmail(email.value())) {
            throw new EmailAlreadyInUseException(
                    UserErrorMessages.EMAIL_ALREADY_EXISTS,
                    DomainException.ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        PlainPassword plainPassword = new PlainPassword(command.password());
        PasswordHash hash = encryptionRepository.encode(plainPassword.value());
        String username = email.value().split(AT_SYMBOL)[0];

        User user = new User();
        user.setName(new PersonName(command.name()));
        user.setSurname(new PersonName(command.surname()));
        user.setUsername(new Username(username));
        user.setEmail(email);
        user.setPasswordHash(hash);

        var savedUser = userRepository.save(user);
        var userId = savedUser.getId();

        UserPreference userPreference = UserPreference.defaultFor(userId);

        userPreferenceRepository.save(userPreference);

        return new UserResult(
                userId.value().toString(),
                email.value()
        );
    }
}
