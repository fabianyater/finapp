create extension if not exists pgcrypto;

-- =========================
-- Users
-- =========================
create table if not exists users
(
    id            uuid primary key      default gen_random_uuid(),
    name          varchar(255) not null,
    surname       varchar(255) not null,
    username      varchar(255) not null,
    email         varchar(255) not null,
    password_hash varchar(255) not null,
    created_at    timestamptz  not null default now(),
    updated_at    timestamptz  not null default now(),

    constraint uk_user_email unique (email),
    constraint uk_user_username unique (username)
);

-- =========================
-- User preferences (1:1)
-- =========================
create table if not exists user_preferences
(
    user_id           uuid        not null,
    locale            varchar(32) not null default 'es-CO',
    currency          varchar(3)  not null default 'COP',
    timezone          varchar(64) not null default 'America/Bogota',
    dark_mode         boolean     not null default false,
    first_day_of_week smallint    not null default 1,
    date_format       varchar(20) not null default 'yyyy-MM-dd',

    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now(),

    constraint chk_first_day_of_week
        check (first_day_of_week between 1 and 7),

    constraint chk_preferences_currency
        check (currency ~ '^[A-Z]{3}$'),

    constraint pk_user_preferences primary key (user_id),

    constraint fk_user_preferences_user
        foreign key (user_id)
            references users (id)
            on delete cascade
);

-- =========================
-- Accounts
-- =========================
create table if not exists accounts
(
    id                 uuid primary key     default gen_random_uuid(),
    user_id            uuid        not null references users (id) on delete cascade,

    name               text        not null,
    type               text        not null,                   -- CASH | BANK | CARD
    initial_balance    bigint      not null default 0,
    currency           varchar(3)  not null default 'COP',

    icon               text        not null default 'wallet',
    color              varchar(7)  not null default '#004ab3', -- HEX #RRGGBB

    is_default         boolean     not null default false,
    is_archived        boolean     not null default false,
    exclude_from_total boolean     not null default false,

    created_at         timestamptz not null default now(),
    updated_at         timestamptz not null default now(),

    -- needed for composite FK from transactions (user_id, account_id) -> accounts(user_id, id)
    constraint uq_accounts_user_id_id unique (user_id, id),

    constraint chk_account_color_hex
        check (color ~ '^#[0-9A-Fa-f]{6}$'),

    constraint chk_account_name_not_blank
        check (length(trim(name)) > 0),

    constraint chk_account_currency
        check (currency ~ '^[A-Z]{3}$'),

    constraint chk_account_type
        check (type in ('CASH', 'BANK', 'CARD'))
);

create index if not exists idx_accounts_user on accounts (user_id);

-- Ensure at most one default account per user
create unique index if not exists uq_accounts_default_per_user
    on accounts (user_id)
    where is_default = true;

-- Avoid duplicate account names (case-insensitive) for active accounts
create unique index if not exists uq_accounts_user_name
    on accounts (user_id, lower(name))
    where is_archived = false;

-- =========================
-- Categories
-- =========================
create table if not exists categories
(
    id         uuid primary key     default gen_random_uuid(),
    user_id    uuid        not null references users (id) on delete cascade,

    name       text        not null,
    type       text        not null, -- EXPENSE | INCOME
    color      varchar(7)  not null default '#64748b',
    icon       varchar(32) not null default 'tag',

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),

    -- needed for composite FK from transactions (user_id, category_id) -> categories(user_id, id)
    constraint uq_categories_user_id_id unique (user_id, id),

    constraint chk_category_type
        check (type in ('EXPENSE', 'INCOME')),

    constraint chk_category_name_not_blank
        check (length(trim(name)) > 0),

    constraint chk_category_color_hex
        check (color ~ '^#[0-9A-Fa-f]{6}$')
);

create index if not exists idx_categories_user on categories (user_id);

-- Avoid duplicate category names per user+type (case-insensitive)
create unique index if not exists uq_categories_user_type_name
    on categories (user_id, type, lower(name));

-- =========================
-- Transactions
-- =========================
create table if not exists transactions
(
    id          uuid primary key     default gen_random_uuid(),
    user_id     uuid        not null references users (id) on delete cascade,

    type        text        not null, -- EXPENSE | INCOME
    amount      bigint      not null, -- always positive
    currency    varchar(3)  not null default 'COP',

    account_id  uuid        not null,
    category_id uuid        not null,

    occurred_on timestamptz not null,
    description text        not null,
    note        text        null,

    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),

    -- Multi-tenant safe FKs (prevents referencing another user's account/category)
    constraint fk_tx_account_user
        foreign key (user_id, account_id)
            references accounts (user_id, id),

    constraint fk_tx_category_user
        foreign key (user_id, category_id)
            references categories (user_id, id),

    constraint chk_transaction_type
        check (type in ('EXPENSE', 'INCOME')),

    constraint chk_amount_positive
        check (amount > 0),

    constraint chk_tx_currency
        check (currency ~ '^[A-Z]{3}$'),

    constraint chk_description_not_blank
        check (length(trim(description)) > 0)
);

-- Indices for list + filters (ordered)
create index if not exists idx_tx_user_date_created
    on transactions (user_id, occurred_on desc, created_at desc);

create index if not exists idx_tx_user_account_date_created
    on transactions (user_id, account_id, occurred_on desc, created_at desc);

create index if not exists idx_tx_user_category_date_created
    on transactions (user_id, category_id, occurred_on desc, created_at desc);

create index if not exists idx_tx_user_type_date_created
    on transactions (user_id, type, occurred_on desc, created_at desc);
