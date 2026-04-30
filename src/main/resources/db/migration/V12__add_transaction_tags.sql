CREATE TABLE transaction_tags (
    transaction_id UUID        NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    tag            VARCHAR(100) NOT NULL
);

CREATE INDEX idx_transaction_tags_transaction_id ON transaction_tags(transaction_id);
