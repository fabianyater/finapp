ALTER TABLE accounts
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE transactions
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Opcional: índices para mejorar performance
CREATE INDEX idx_accounts_version ON accounts(id, version);
CREATE INDEX idx_transactions_version ON transactions(id, version);