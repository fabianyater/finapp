package com.fyr.finapp.adapters.config;

import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.AccountAdapter;
import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.CategoryAdapter;
import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.UserAdapter;
import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.UserPreferenceAdapter;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IAccountMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.ICategoryEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IUserEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.IUserPreferenceEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.AccountJpaRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.CategoryJpaRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserJpaRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserPreferenceJpaRepository;
import com.fyr.finapp.adapters.driven.security.auth.AuthenticationAdapter;
import com.fyr.finapp.adapters.driven.security.encryption.EncryptionAdapter;
import com.fyr.finapp.adapters.driven.security.jwt.JwtProvider;
import com.fyr.finapp.application.usecase.account.*;
import com.fyr.finapp.application.usecase.auth.AuthenticationService;
import com.fyr.finapp.application.usecase.category.CreateCategoryService;
import com.fyr.finapp.application.usecase.category.ListAccountService;
import com.fyr.finapp.application.usecase.user.UserService;
import com.fyr.finapp.domain.api.account.*;
import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.api.category.CreateCategoryUseCase;
import com.fyr.finapp.domain.api.category.ListCategoriesUseCase;
import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.auth.IEncryptionRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
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
    public IUserRepository userRepositoryPort(UserJpaRepository userJpaRepository, IUserEntityMapper userMapper) {
        return new UserAdapter(userJpaRepository, userMapper);
    }

    @Bean
    public IUserPreferenceRepository preferenceRepositoryPort(UserPreferenceJpaRepository userPreferenceJpaRepository, EntityManager em) {
        return new UserPreferenceAdapter(userPreferenceJpaRepository, userPreferenceEntityMapper, em);
    }

    @Bean
    public IAccountRepository accountRepositoryPort(AccountJpaRepository accountJpaRepository, EntityManager em, IAccountMapper accountMapper) {
        return new AccountAdapter(accountJpaRepository, em, accountMapper);
    }

    @Bean
    public ICategoryRepository categoryRepositoryPort(CategoryJpaRepository repo, EntityManager em, ICategoryEntityMapper categoryMapper) {
        return new CategoryAdapter(repo, em, categoryMapper);
    }

    // ----- Use Cases (Application layer) -----

    @Bean
    public AccountValidator accountValidator(IAccountRepository accountRepository) {
        return new AccountValidator(accountRepository);
    }

    @Bean
    public AuthenticateUseCase authenticateUseCase(IAuthenticationRepository authenticationRepository) {
        return new AuthenticationService(authenticationRepository);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(
            IUserRepository userRepositoryPort,
            IUserPreferenceRepository preferenceRepositoryPort,
            ICategoryRepository categoryRepository,
            IAccountRepository accountRepository,
            IEncryptionRepository encryptionRepository,
            TransactionalExecutor tx) {
        CreateUserUseCase core = new UserService(userRepositoryPort, preferenceRepositoryPort, categoryRepository, accountRepository, encryptionRepository);

        return command -> tx.required(() -> core.create(command));
    }

    @Bean
    public CreateAccountUseCase createAccountUseCase(
            IAccountRepository accountRepository,
            IAuthenticationRepository authenticationRepository,
            TransactionalExecutor tx) {
        CreateAccountUseCase core = new AccountService(accountRepository, authenticationRepository);
        return command -> tx.required(() -> core.create(command));
    }

    @Bean
    public ListAccountsUseCase listAccountsUseCase(IAccountRepository accountRepository, IAuthenticationRepository authenticationRepository) {
        return new ListAccountsService(accountRepository, authenticationRepository);
    }

    @Bean
    public UpdateAccountUseCase updateAccountUseCase(
            IAccountRepository accountRepository,
            IAuthenticationRepository authenticationRepository,
            AccountValidator accountValidator) {
        return new UpdateAccountService(accountRepository, authenticationRepository, accountValidator);
    }

    @Bean
    public ArchiveAccountUseCase archiveAccountUseCase(
            IAccountRepository accountRepository,
            IAuthenticationRepository authenticationRepository,
            AccountValidator accountValidator
    ) {
        return new ArchiveAccountService(accountRepository, authenticationRepository, accountValidator);
    }

    @Bean
    public AccountDetailsUseCase accountDetailsUseCase(
            IAccountRepository accountRepository,
            IAuthenticationRepository authenticationRepository,
            AccountValidator accountValidator) {
        return new AccountDetailsService(accountRepository, authenticationRepository, accountValidator);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(
            ICategoryRepository categoryRepository,
            IAuthenticationRepository authenticationRepository
    ) {
        return new CreateCategoryService(categoryRepository, authenticationRepository);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase(
            ICategoryRepository categoryRepository,
            IAuthenticationRepository authenticationRepository
    ) {
        return new ListAccountService(categoryRepository, authenticationRepository);
    }
}
