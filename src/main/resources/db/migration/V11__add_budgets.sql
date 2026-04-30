CREATE TABLE budgets (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID   NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID   NOT NULL,
    limit_amount BIGINT NOT NULL CHECK (limit_amount > 0),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_budget_category_user
        FOREIGN KEY (user_id, category_id)
            REFERENCES categories(user_id, id) ON DELETE CASCADE,

    CONSTRAINT uq_budget_user_category
        UNIQUE (user_id, category_id)
);

CREATE INDEX idx_budgets_user ON budgets(user_id);
