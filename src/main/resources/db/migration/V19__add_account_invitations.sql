CREATE TABLE account_invitations (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID        NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    inviter_id   UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invitee_id   UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED')),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    responded_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX idx_account_invitations_pending
    ON account_invitations(account_id, invitee_id)
    WHERE status = 'PENDING';

CREATE INDEX idx_account_invitations_invitee ON account_invitations(invitee_id, status);
