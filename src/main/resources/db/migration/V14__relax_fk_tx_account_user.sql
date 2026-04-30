-- Drop the composite FK that required transaction.user_id = account.user_id.
-- Now that accounts can be shared, a member (different user_id) can create
-- transactions on an account they don't own.
ALTER TABLE transactions DROP CONSTRAINT fk_tx_account_user;

-- Keep referential integrity: account_id must still reference a valid account.
ALTER TABLE transactions
    ADD CONSTRAINT fk_tx_account_id
        FOREIGN KEY (account_id)
            REFERENCES accounts (id)
            ON DELETE CASCADE;
