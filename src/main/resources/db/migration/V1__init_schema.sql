CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- Users
-- =========================
CREATE TABLE IF NOT EXISTS users
(
    id                       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name                     VARCHAR(255) NOT NULL,
    surname                  VARCHAR(255) NOT NULL,
    username                 VARCHAR(255) NOT NULL,
    email                    VARCHAR(255) NOT NULL,
    password_hash            VARCHAR(255) NOT NULL,
    refresh_token            VARCHAR(255),
    refresh_token_expires_at TIMESTAMPTZ,
    created_at               TIMESTAMPTZ  NOT NULL    DEFAULT now(),
    updated_at               TIMESTAMPTZ  NOT NULL    DEFAULT now(),

    CONSTRAINT uk_user_email    UNIQUE (email),
    CONSTRAINT uk_user_username UNIQUE (username)
);

-- =========================
-- User preferences (1:1)
-- =========================
CREATE TABLE IF NOT EXISTS user_preferences
(
    user_id           UUID        NOT NULL,
    locale            VARCHAR(32) NOT NULL DEFAULT 'es-CO',
    currency          VARCHAR(3)  NOT NULL DEFAULT 'COP',
    timezone          VARCHAR(64) NOT NULL DEFAULT 'America/Bogota',
    theme             VARCHAR(10) NOT NULL DEFAULT 'system',
    first_day_of_week SMALLINT    NOT NULL DEFAULT 1,
    date_format       VARCHAR(20) NOT NULL DEFAULT 'yyyy-MM-dd',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_first_day_of_week    CHECK (first_day_of_week BETWEEN 1 AND 7),
    CONSTRAINT chk_preferences_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT chk_preferences_theme    CHECK (theme IN ('light', 'dark', 'system')),

    CONSTRAINT pk_user_preferences PRIMARY KEY (user_id),
    CONSTRAINT fk_user_preferences_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- =========================
-- Accounts
-- =========================
CREATE TABLE IF NOT EXISTS accounts
(
    id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name               TEXT        NOT NULL,
    type               TEXT        NOT NULL,
    initial_balance    BIGINT      NOT NULL DEFAULT 0,
    current_balance    BIGINT      NOT NULL DEFAULT 0,
    currency           VARCHAR(3)  NOT NULL DEFAULT 'COP',
    icon               TEXT        NOT NULL DEFAULT 'wallet',
    color              VARCHAR(7)  NOT NULL DEFAULT '#004ab3',
    is_default         BOOLEAN     NOT NULL DEFAULT FALSE,
    is_archived        BOOLEAN     NOT NULL DEFAULT FALSE,
    exclude_from_total BOOLEAN     NOT NULL DEFAULT FALSE,
    version            BIGINT      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_accounts_user_id_id     UNIQUE (user_id, id),
    CONSTRAINT chk_account_color_hex      CHECK (color ~ '^#[0-9A-Fa-f]{6}$'),
    CONSTRAINT chk_account_name_not_blank CHECK (length(trim(name)) > 0),
    CONSTRAINT chk_account_currency       CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT chk_account_type           CHECK (type IN ('CASH', 'BANK', 'CARD'))
);

CREATE INDEX IF NOT EXISTS idx_accounts_user
    ON accounts (user_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_accounts_default_per_user
    ON accounts (user_id)
    WHERE is_default = TRUE;

CREATE UNIQUE INDEX IF NOT EXISTS uq_accounts_user_name
    ON accounts (user_id, lower(name))
    WHERE is_archived = FALSE;

CREATE INDEX IF NOT EXISTS idx_accounts_version
    ON accounts (id, version);

-- =========================
-- Categories
-- =========================
CREATE TABLE IF NOT EXISTS categories
(
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name       TEXT        NOT NULL,
    type       TEXT        NOT NULL,
    color      VARCHAR(7)  NOT NULL DEFAULT '#64748b',
    icon       VARCHAR(32) NOT NULL DEFAULT 'tag',
    is_deleted BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_categories_user_id_id      UNIQUE (user_id, id),
    CONSTRAINT chk_category_type             CHECK (type IN ('EXPENSE', 'INCOME')),
    CONSTRAINT chk_category_name_not_blank   CHECK (length(trim(name)) > 0),
    CONSTRAINT chk_category_color_hex        CHECK (color ~ '^#[0-9A-Fa-f]{6}$')
);

CREATE INDEX IF NOT EXISTS idx_categories_user
    ON categories (user_id);

CREATE INDEX IF NOT EXISTS idx_categories_is_deleted
    ON categories (is_deleted);

CREATE UNIQUE INDEX IF NOT EXISTS uq_categories_user_type_name_active
    ON categories (user_id, type, lower(name))
    WHERE is_deleted = FALSE;

-- =========================
-- Transactions
-- =========================
CREATE TABLE IF NOT EXISTS transactions
(
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    type          TEXT        NOT NULL,
    amount        BIGINT      NOT NULL,
    currency      VARCHAR(3)  NOT NULL DEFAULT 'COP',
    account_id    UUID        NOT NULL,
    to_account_id UUID                 REFERENCES accounts (id),
    category_id   UUID,
    occurred_on   TIMESTAMPTZ NOT NULL,
    description   TEXT        NOT NULL,
    note          TEXT,
    version       BIGINT      NOT NULL DEFAULT 0,
    deleted_at    TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_tx_account_id
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,

    CONSTRAINT fk_tx_category_user
        FOREIGN KEY (user_id, category_id) REFERENCES categories (user_id, id),

    CONSTRAINT chk_transaction_type      CHECK (type IN ('EXPENSE', 'INCOME', 'TRANSFER')),
    CONSTRAINT chk_amount_positive       CHECK (amount > 0),
    CONSTRAINT chk_tx_currency           CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT chk_description_not_blank CHECK (length(trim(description)) > 0)
);

CREATE INDEX IF NOT EXISTS idx_tx_user_date_created
    ON transactions (user_id, occurred_on DESC, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_tx_user_account_date_created
    ON transactions (user_id, account_id, occurred_on DESC, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_tx_user_category_date_created
    ON transactions (user_id, category_id, occurred_on DESC, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_tx_user_type_date_created
    ON transactions (user_id, type, occurred_on DESC, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_transactions_version
    ON transactions (id, version);

CREATE INDEX IF NOT EXISTS idx_transactions_deleted_at
    ON transactions (deleted_at);

-- =========================
-- Transaction tags
-- =========================
CREATE TABLE IF NOT EXISTS transaction_tags
(
    transaction_id UUID         NOT NULL REFERENCES transactions (id) ON DELETE CASCADE,
    tag            VARCHAR(100) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_transaction_tags_transaction_id
    ON transaction_tags (transaction_id);

-- =========================
-- Recurring transactions
-- =========================
CREATE TABLE IF NOT EXISTS recurring_transactions
(
    id                UUID        PRIMARY KEY,
    user_id           UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    account_id        UUID        NOT NULL,
    to_account_id     UUID,
    category_id       UUID,
    type              VARCHAR(20) NOT NULL CHECK (type IN ('EXPENSE', 'INCOME', 'TRANSFER')),
    amount            BIGINT      NOT NULL CHECK (amount > 0),
    currency          VARCHAR(3)  NOT NULL DEFAULT 'COP',
    description       TEXT        NOT NULL,
    note              TEXT,
    frequency         VARCHAR(20) NOT NULL CHECK (frequency IN ('DAILY', 'WEEKLY', 'BIWEEKLY', 'MONTHLY', 'YEARLY')),
    next_due_date     DATE        NOT NULL,
    last_generated_at TIMESTAMPTZ,
    is_active         BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at        TIMESTAMPTZ,

    FOREIGN KEY (user_id, account_id) REFERENCES accounts (user_id, id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_rt_user
    ON recurring_transactions (user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_rt_next_due
    ON recurring_transactions (next_due_date)
    WHERE is_active = TRUE AND deleted_at IS NULL;

-- =========================
-- Budgets
-- =========================
CREATE TABLE IF NOT EXISTS budgets
(
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    category_id  UUID        NOT NULL,
    limit_amount BIGINT      NOT NULL CHECK (limit_amount > 0),
    created_at   TIMESTAMPTZ NOT NULL    DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL    DEFAULT now(),

    CONSTRAINT fk_budget_category_user
        FOREIGN KEY (user_id, category_id) REFERENCES categories (user_id, id) ON DELETE CASCADE,

    CONSTRAINT uq_budget_user_category UNIQUE (user_id, category_id)
);

CREATE INDEX IF NOT EXISTS idx_budgets_user
    ON budgets (user_id);

-- =========================
-- Account members
-- =========================
CREATE TABLE IF NOT EXISTS account_members
(
    account_id UUID        NOT NULL REFERENCES accounts (id) ON DELETE CASCADE,
    user_id    UUID        NOT NULL REFERENCES users (id)    ON DELETE CASCADE,
    invited_by UUID                 REFERENCES users (id)    ON DELETE SET NULL,
    joined_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY (account_id, user_id)
);

-- =========================
-- Account invitations
-- =========================
CREATE TABLE IF NOT EXISTS account_invitations
(
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID        NOT NULL REFERENCES accounts (id)  ON DELETE CASCADE,
    inviter_id   UUID        NOT NULL REFERENCES users (id)     ON DELETE CASCADE,
    invitee_id   UUID        NOT NULL REFERENCES users (id)     ON DELETE CASCADE,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED')),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    responded_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_account_invitations_pending
    ON account_invitations (account_id, invitee_id)
    WHERE status = 'PENDING';

CREATE INDEX IF NOT EXISTS idx_account_invitations_invitee
    ON account_invitations (invitee_id, status);

-- =========================
-- Notifications
-- =========================
CREATE TABLE IF NOT EXISTS notifications
(
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    type       VARCHAR(50)  NOT NULL,
    title      VARCHAR(255) NOT NULL,
    body       VARCHAR(500),
    metadata   JSONB,
    read_at    TIMESTAMPTZ,
    created_at TIMESTAMPTZ  NOT NULL    DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_created
    ON notifications (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_notifications_user_unread
    ON notifications (user_id)
    WHERE read_at IS NULL;
