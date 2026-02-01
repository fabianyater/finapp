package com.fyr.finapp.adapters.config;

import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.UserAdapter;
import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.UserPreferenceAdapter;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IUserEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IUserPreferenceEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserJpaRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserPreferenceJpaRepository;
import com.fyr.finapp.adapters.driven.security.auth.AuthenticationAdapter;
import com.fyr.finapp.adapters.driven.security.encryption.EncryptionAdapter;
import com.fyr.finapp.adapters.driven.security.jwt.JwtProvider;
import com.fyr.finapp.application.usecase.auth.AuthenticationService;
import com.fyr.finapp.application.usecase.user.UserService;
import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.auth.IEncryptionRepository;
import com.fyr.finapp.domain.spi.user.IUserPreferenceRepository;
import com.fyr.finapp.domain.spi.user.IUserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final IUserEntityMapper userEntityMapper;
    private final IUserPreferenceEntityMapper userPreferenceEntityMapper;
    // ----- Adapters (implement Ports) -----

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager txManager) {
        return new TransactionTemplate(txManager);
    }

    @Bean
    public TransactionalExecutor transactionalExecutor(TransactionTemplate txTemplate) {
        return new TransactionalExecutor(txTemplate);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IEncryptionRepository encryptionRepository(PasswordEncoder passwordEncoder) {
        return new EncryptionAdapter(passwordEncoder);
    }

    @Bean
    public IAuthenticationRepository authenticationRepository(
            AuthenticationManager authenticationManager,
            JwtProvider jwtProvider
    ) {
        return new AuthenticationAdapter(authenticationManager, jwtProvider);
    }

    @Bean
    public IUserRepository userRepositoryPort(UserJpaRepository userJpaRepository) {
        return new UserAdapter(userJpaRepository, userEntityMapper);
    }

    @Bean
    public IUserPreferenceRepository preferenceRepositoryPort(UserPreferenceJpaRepository userPreferenceJpaRepository, EntityManager em) {
        return new UserPreferenceAdapter(userPreferenceJpaRepository, userPreferenceEntityMapper, em);
    }

    // ----- Use Cases (Application layer) -----

    @Bean
    public AuthenticateUseCase authenticateUseCase(IAuthenticationRepository authenticationRepository) {
        return new AuthenticationService(authenticationRepository);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(
            IUserRepository userRepositoryPort,
            IUserPreferenceRepository preferenceRepositoryPort,
            IEncryptionRepository encryptionRepository,
            TransactionalExecutor tx) {
        CreateUserUseCase core = new UserService(userRepositoryPort, preferenceRepositoryPort, encryptionRepository);

        return command -> tx.required(() -> core.create(command));
    }
}
