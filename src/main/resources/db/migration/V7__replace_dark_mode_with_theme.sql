alter table user_preferences
    drop column dark_mode;

alter table user_preferences
    add column theme varchar(10) not null default 'system';

alter table user_preferences
    add constraint chk_preferences_theme
        check (theme in ('light', 'dark', 'system'));
