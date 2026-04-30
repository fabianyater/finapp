package com.fyr.finapp.adapters.config;

import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.*;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.*;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.*;
import com.fyr.finapp.application.usecase.account.*;
import com.fyr.finapp.domain.api.account.*;
import com.fyr.finapp.domain.spi.account.IAccountInvitationRepository;
import com.fyr.finapp.application.usecase.notification.BudgetAlertChecker;
import com.fyr.finapp.application.usecase.notification.NotificationService;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import com.fyr.finapp.application.usecase.recurring.*;
import com.fyr.finapp.domain.api.recurring.*;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;
import com.fyr.finapp.adapters.driven.security.auth.AuthenticationAdapter;
import com.fyr.finapp.adapters.driven.security.encryption.EncryptionAdapter;
import com.fyr.finapp.adapters.driven.security.jwt.JwtProperties;
import com.fyr.finapp.adapters.driven.security.jwt.JwtProvider;
import com.fyr.finapp.application.usecase.account.*;
import com.fyr.finapp.application.usecase.auth.AuthenticationService;
import com.fyr.finapp.application.usecase.category.*;
import com.fyr.finapp.domain.api.category.GetCategorySummaryUseCase;
import com.fyr.finapp.domain.api.category.GetCategoryTemplatesUseCase;
import com.fyr.finapp.domain.api.category.SetupCategoriesUseCase;
import com.fyr.finapp.application.usecase.transaction.*;
import com.fyr.finapp.domain.api.transaction.CreateTransferUseCase;
import com.fyr.finapp.domain.api.transaction.DeleteTransferPairUseCase;
import com.fyr.finapp.application.usecase.user.DeleteUserService;
import com.fyr.finapp.application.usecase.user.UpdatePreferencesService;
import com.fyr.finapp.application.usecase.user.UpdateProfileService;
import com.fyr.finapp.application.usecase.user.UserDetailsService;
import com.fyr.finapp.application.usecase.user.UserService;
import com.fyr.finapp.domain.api.account.*;
import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import com.fyr.finapp.domain.api.category.*;
import com.fyr.finapp.domain.api.transaction.*;
import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import com.fyr.finapp.domain.api.user.DeleteUserUseCase;
import com.fyr.finapp.domain.api.user.UpdatePreferencesUseCase;
import com.fyr.finapp.domain.api.user.UpdateProfileUseCase;
import com.fyr.finapp.domain.api.user.UserDetailsUseCase;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.auth.IEncryptionRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import com.fyr.finapp.adapters.driven.persistence.jpa.adapter.BudgetAdapter;
import com.fyr.finapp.application.usecase.budget.*;
import com.fyr.finapp.domain.api.budget.*;
import com.fyr.finapp.domain.spi.budget.IBudgetRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
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
            JwtProvider jwtProvider,
            JwtProperties jwtProperties,
            UserJpaRepository userJpaRepository,
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService
    ) {
        return new AuthenticationAdapter(authenticationManager, jwtProvider, jwtProperties, userJpaRepository, userDetailsService);
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
    public IAccountRepository accountRepositoryPort(AccountJpaRepository accountJpaRepository, AccountMemberJpaRepository accountMemberJpaRepository, EntityManager em, IAccountMapper accountMapper) {
        return new AccountAdapter(accountJpaRepository, accountMemberJpaRepository, em, accountMapper);
    }

    @Bean
    public ICategoryRepository categoryRepositoryPort(CategoryJpaRepository repo, EntityManager em, ICategoryEntityMapper categoryMapper) {
        return new CategoryAdapter(repo, em, categoryMapper);
    }

    @Bean
    public ITransactionRepository transactionRepository(
            ITransactionMapper transactionMapper,
            TransactionJpaRepository transactionJpaRepository,
            EntityManager em
    ) {
        return new TransactionAdapter(transactionMapper, transactionJpaRepository, em);
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
            IAccountRepository accountRepository,
            IEncryptionRepository encryptionRepository,
            TransactionalExecutor tx) {
        CreateUserUseCase core = new UserService(userRepositoryPort, preferenceRepositoryPort, accountRepository, encryptionRepository);

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
    public UnarchiveAccountUseCase unarchiveAccountUseCase(
            IAccountRepository accountRepository,
            IAuthenticationRepository authenticationRepository,
            AccountValidator accountValidator
    ) {
        return new UnarchiveAccountService(accountRepository, authenticationRepository, accountValidator);
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

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(
            ICategoryRepository categoryRepository,
            IAuthenticationRepository authenticationRepository
    ) {
        return new UpdateCategoryService(categoryRepository, authenticationRepository);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(
            ICategoryRepository categoryRepository,
            IAuthenticationRepository authenticationRepository
    ) {
        return new DeleteCategoryService(categoryRepository, authenticationRepository);
    }

    @Bean
    public RestoreCategoryUseCase restoreCategoryUseCase(
            IAuthenticationRepository authenticationRepository,
            ICategoryRepository categoryRepository
    ) {
        return new RestoreCategoryService(authenticationRepository, categoryRepository);
    }

    @Bean
    public GetCategoryTemplatesUseCase getCategoryTemplatesUseCase() {
        return new GetCategoryTemplatesService();
    }

    @Bean
    public SetupCategoriesUseCase setupCategoriesUseCase(
            ICategoryRepository categoryRepository,
            IAuthenticationRepository authenticationRepository
    ) {
        return new SetupCategoriesService(categoryRepository, authenticationRepository);
    }

    @Bean
    public GetCategorySummaryUseCase getCategorySummaryUseCase(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository
    ) {
        return new GetCategorySummaryService(transactionRepository, authenticationRepository);
    }

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            ICategoryRepository categoryRepository,
            AccountValidator accountValidator,
            BudgetAlertChecker budgetAlertChecker
    ) {
        return new CreateTransactionService(
                authenticationRepository,
                transactionRepository,
                accountRepository,
                categoryRepository,
                accountValidator,
                budgetAlertChecker
        );
    }

    @Bean
    public UpdateTransactionUseCase updateTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            ICategoryRepository categoryRepository,
            AccountValidator accountValidator,
            BudgetAlertChecker budgetAlertChecker) {
        return new UpdateTransactionService(
                authenticationRepository,
                transactionRepository,
                accountRepository,
                categoryRepository,
                accountValidator,
                budgetAlertChecker
        );
    }

    @Bean
    public ListTransactionUseCase listTransactionUseCase(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository) {
        return new ListTransactionService(transactionRepository,
                authenticationRepository);
    }

    @Bean
    public ListTransactionTagsUseCase listTransactionTagsUseCase(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository) {
        return new ListTransactionTagsService(transactionRepository, authenticationRepository);
    }

    @Bean
    public RenameTagUseCase renameTagUseCase(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository) {
        return new RenameTagService(transactionRepository, authenticationRepository);
    }

    @Bean
    public DeleteTagUseCase deleteTagUseCase(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository) {
        return new DeleteTagService(transactionRepository, authenticationRepository);
    }

    @Bean
    public ExportTransactionsCsvUseCase exportTransactionsCsvUseCase(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository) {
        return new ExportTransactionsCsvService(transactionRepository, authenticationRepository);
    }

    @Bean
    public TransactionDetailsUseCase transactionDetailsUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            ICategoryRepository categoryRepository,
            AccountValidator accountValidator) {
        return new TransactionDetailsService(
                authenticationRepository,
                transactionRepository,
                categoryRepository,
                accountValidator);
    }

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        return new DeleteTransactionService(
                authenticationRepository,
                transactionRepository,
                accountRepository,
                accountValidator);
    }

    @Bean
    public ListDeletedTransactionsUseCase listDeletedTransactionsUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository) {
        return new ListDeletedTransactionsService(authenticationRepository, transactionRepository);
    }

    @Bean
    public RestoreTransactionUseCase restoreTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        return new RestoreTransactionService(
                authenticationRepository,
                transactionRepository,
                accountRepository,
                accountValidator);
    }

    @Bean
    public DeleteTransferPairUseCase deleteTransferPairUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        return new DeleteTransferPairService(authenticationRepository, transactionRepository, accountRepository, accountValidator);
    }

    @Bean
    public CreateTransferUseCase createTransferUseCase(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        return new CreateTransferService(
                authenticationRepository,
                transactionRepository,
                accountRepository,
                accountValidator);
    }

    @Bean
    public IRecurringTransactionRepository recurringTransactionRepository(
            IRecurringTransactionMapper mapper,
            RecurringTransactionJpaRepository jpaRepository,
            EntityManager em) {
        return new RecurringTransactionAdapter(mapper, jpaRepository, em);
    }

    @Bean
    public CreateRecurringTransactionUseCase createRecurringTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository,
            IAccountRepository accountRepository,
            ICategoryRepository categoryRepository,
            AccountValidator accountValidator) {
        return new CreateRecurringTransactionService(
                authenticationRepository, recurringTransactionRepository,
                accountRepository, categoryRepository, accountValidator);
    }

    @Bean
    public UpdateRecurringTransactionUseCase updateRecurringTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository,
            IAccountRepository accountRepository,
            ICategoryRepository categoryRepository,
            AccountValidator accountValidator) {
        return new UpdateRecurringTransactionService(
                authenticationRepository, recurringTransactionRepository,
                accountRepository, categoryRepository, accountValidator);
    }

    @Bean
    public DeleteRecurringTransactionUseCase deleteRecurringTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository) {
        return new DeleteRecurringTransactionService(authenticationRepository, recurringTransactionRepository);
    }

    @Bean
    public ListRecurringTransactionsUseCase listRecurringTransactionsUseCase(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository) {
        return new ListRecurringTransactionsService(authenticationRepository, recurringTransactionRepository);
    }

    @Bean
    public ToggleRecurringTransactionUseCase toggleRecurringTransactionUseCase(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository) {
        return new ToggleRecurringTransactionService(authenticationRepository, recurringTransactionRepository);
    }

    @Bean
    public ProcessRecurringTransactionsService processRecurringTransactionsService(
            IRecurringTransactionRepository recurringTransactionRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            INotificationRepository notificationRepository) {
        return new ProcessRecurringTransactionsService(
                recurringTransactionRepository, transactionRepository, accountRepository, notificationRepository);
    }

    @Bean
    public DeleteAccountUseCase deleteAccountUseCase(
            IAuthenticationRepository authenticationRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        return new DeleteAccountService(
                accountRepository,
                authenticationRepository,
                accountValidator);
    }

    @Bean
    public IAccountInvitationRepository accountInvitationRepository(
            AccountInvitationJpaRepository accountInvitationJpaRepository) {
        return new AccountInvitationAdapter(accountInvitationJpaRepository);
    }

    @Bean
    public InviteMemberUseCase inviteMemberUseCase(
            IAuthenticationRepository authenticationRepository,
            IAccountRepository accountRepository,
            IUserRepository userRepository,
            AccountValidator accountValidator,
            IAccountInvitationRepository accountInvitationRepository,
            INotificationRepository notificationRepository) {
        return new InviteMemberService(authenticationRepository, accountRepository, userRepository,
                accountValidator, accountInvitationRepository, notificationRepository);
    }

    @Bean
    public ListPendingInvitationsUseCase listPendingInvitationsUseCase(
            IAuthenticationRepository authenticationRepository,
            IAccountInvitationRepository accountInvitationRepository) {
        return new ListPendingInvitationsService(authenticationRepository, accountInvitationRepository);
    }

    @Bean
    public AcceptInvitationUseCase acceptInvitationUseCase(
            IAuthenticationRepository authenticationRepository,
            IAccountInvitationRepository accountInvitationRepository,
            IAccountRepository accountRepository,
            INotificationRepository notificationRepository) {
        return new AcceptInvitationService(authenticationRepository, accountInvitationRepository,
                accountRepository, notificationRepository);
    }

    @Bean
    public DeclineInvitationUseCase declineInvitationUseCase(
            IAuthenticationRepository authenticationRepository,
            IAccountInvitationRepository accountInvitationRepository) {
        return new DeclineInvitationService(authenticationRepository, accountInvitationRepository);
    }

    @Bean
    public RemoveMemberUseCase removeMemberUseCase(
            IAuthenticationRepository authenticationRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator,
            INotificationRepository notificationRepository) {
        return new RemoveMemberService(authenticationRepository, accountRepository, accountValidator,
                notificationRepository);
    }

    @Bean
    public ListMembersUseCase listMembersUseCase(
            IAuthenticationRepository authenticationRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        return new ListMembersService(authenticationRepository, accountRepository, accountValidator);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(
            IUserRepository userRepository,
            IAuthenticationRepository authenticationRepository) {
        return new DeleteUserService(userRepository, authenticationRepository);
    }

    @Bean
    public UserDetailsUseCase userDetailsUseCase(
            IUserRepository userRepository,
            IUserPreferenceRepository userPreferenceRepository,
            IAuthenticationRepository authenticationRepository) {
        return new UserDetailsService(userRepository, userPreferenceRepository, authenticationRepository);
    }

    @Bean
    public UpdateProfileUseCase updateProfileUseCase(
            IUserRepository userRepository,
            IUserPreferenceRepository userPreferenceRepository,
            IAuthenticationRepository authenticationRepository) {
        return new UpdateProfileService(userRepository, userPreferenceRepository, authenticationRepository);
    }

    @Bean
    public UpdatePreferencesUseCase updatePreferencesUseCase(
            IUserRepository userRepository,
            IUserPreferenceRepository userPreferenceRepository,
            IAuthenticationRepository authenticationRepository) {
        return new UpdatePreferencesService(userRepository, userPreferenceRepository, authenticationRepository);
    }

    // ── Notifications ────────────────────────────────────────────────────────

    @Bean
    public INotificationRepository notificationRepository(NotificationJpaRepository notificationJpaRepository) {
        return new NotificationAdapter(notificationJpaRepository, new com.fasterxml.jackson.databind.ObjectMapper());
    }

    @Bean
    public NotificationService notificationService(
            IAuthenticationRepository authenticationRepository,
            INotificationRepository notificationRepository) {
        return new NotificationService(authenticationRepository, notificationRepository);
    }

    @Bean
    public BudgetAlertChecker budgetAlertChecker(
            BudgetJpaRepository budgetJpaRepository,
            INotificationRepository notificationRepository) {
        return new BudgetAlertChecker(budgetJpaRepository, notificationRepository);
    }

    // ── Budgets ──────────────────────────────────────────────────────────────

    @Bean
    public IBudgetRepository budgetRepository(BudgetJpaRepository budgetJpaRepository) {
        return new BudgetAdapter(budgetJpaRepository);
    }

    @Bean
    public ListBudgetsUseCase listBudgetsUseCase(IAuthenticationRepository authenticationRepository,
                                                  IBudgetRepository budgetRepository) {
        return new ListBudgetsService(authenticationRepository, budgetRepository);
    }

    @Bean
    public CreateBudgetUseCase createBudgetUseCase(IAuthenticationRepository authenticationRepository,
                                                    IBudgetRepository budgetRepository,
                                                    ICategoryRepository categoryRepository) {
        return new CreateBudgetService(authenticationRepository, budgetRepository, categoryRepository);
    }

    @Bean
    public UpdateBudgetUseCase updateBudgetUseCase(IAuthenticationRepository authenticationRepository,
                                                    IBudgetRepository budgetRepository) {
        return new UpdateBudgetService(authenticationRepository, budgetRepository);
    }

    @Bean
    public DeleteBudgetUseCase deleteBudgetUseCase(IAuthenticationRepository authenticationRepository,
                                                    IBudgetRepository budgetRepository) {
        return new DeleteBudgetService(authenticationRepository, budgetRepository);
    }

}
