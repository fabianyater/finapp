ALTER TABLE accounts
    ADD COLUMN current_balance bigint NOT NULL DEFAULT 0;

-- Inicializar con initial_balance
UPDATE accounts SET current_balance = initial_balance;