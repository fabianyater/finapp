CREATE TABLE recurring_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    account_id UUID NOT NULL,
    to_account_id UUID,
    category_id UUID,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EXPENSE', 'INCOME', 'TRANSFER')),
    amount BIGINT NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'COP',
    description TEXT NOT NULL,
    note TEXT,
    frequency VARCHAR(20) NOT NULL CHECK (frequency IN ('DAILY', 'WEEKLY', 'BIWEEKLY', 'MONTHLY', 'YEARLY')),
    next_due_date DATE NOT NULL,
    last_generated_at TIMESTAMPTZ,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (user_id, account_id) REFERENCES accounts(user_id, id) ON DELETE CASCADE
);

CREATE INDEX idx_rt_user ON recurring_transactions(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_rt_next_due ON recurring_transactions(next_due_date) WHERE is_active = true AND deleted_at IS NULL;
