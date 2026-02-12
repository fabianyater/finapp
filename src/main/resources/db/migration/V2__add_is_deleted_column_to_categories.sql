ALTER TABLE public.categories
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_categories_is_deleted ON categories (is_deleted);

DROP INDEX IF EXISTS uq_categories_user_type_name;

CREATE UNIQUE INDEX uq_categories_user_type_name_active
    ON categories (user_id, type, lower(name))
    WHERE is_deleted = FALSE;

COMMENT ON COLUMN categories.is_deleted IS 'Soft delete flag. TRUE means the category is deleted but preserved for historical transactions';

