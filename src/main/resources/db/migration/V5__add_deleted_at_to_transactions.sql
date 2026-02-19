ALTER TABLE transactions
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL;

CREATE INDEX idx_transactions_deleted_at ON transactions (deleted_at);