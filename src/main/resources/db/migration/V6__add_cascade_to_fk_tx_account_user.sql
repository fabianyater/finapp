ALTER TABLE transactions
    DROP CONSTRAINT fk_tx_account_user;

ALTER TABLE transactions
    ADD CONSTRAINT fk_tx_account_user
        FOREIGN KEY (user_id, account_id)
            REFERENCES accounts (user_id, id)
            ON DELETE CASCADE;