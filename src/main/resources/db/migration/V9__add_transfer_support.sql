ALTER TABLE transactions ALTER COLUMN category_id DROP NOT NULL;
ALTER TABLE transactions ADD COLUMN to_account_id UUID REFERENCES accounts(id);
ALTER TABLE transactions DROP CONSTRAINT chk_transaction_type;
ALTER TABLE transactions ADD CONSTRAINT chk_transaction_type CHECK (type IN ('EXPENSE', 'INCOME', 'TRANSFER'));
