package com.fyr.finapp.domain.model.user;

import com.fyr.finapp.domain.shared.vo.Currency;
import com.fyr.finapp.domain.model.user.vo.*;

import java.time.OffsetDateTime;

public class UserPreference {
    private PreferenceId id;
    private LocaleTag locale;
    private Currency currency;
    private TimezoneId timezone;
    private String theme;
    private FirstDayOfWeek firstDayOfWeek;
    private DateFormatPattern dateFormat;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UserId user;

    public UserPreference() {
    }

    public UserPreference(PreferenceId id, LocaleTag locale, Currency currency, TimezoneId timezone, String theme, FirstDayOfWeek firstDayOfWeek, DateFormatPattern dateFormat, OffsetDateTime createdAt, OffsetDateTime updatedAt, UserId user) {
        this.id = id;
        this.locale = locale;
        this.currency = currency;
        this.timezone = timezone;
        this.theme = theme;
        this.firstDayOfWeek = firstDayOfWeek;
        this.dateFormat = dateFormat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    public PreferenceId getId() {
        return id;
    }

    public void setId(PreferenceId id) {
        this.id = id;
    }

    public LocaleTag getLocale() {
        return locale;
    }

    public void setLocale(LocaleTag locale) {
        this.locale = locale;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TimezoneId getTimezone() {
        return timezone;
    }

    public void setTimezone(TimezoneId timezone) {
        this.timezone = timezone;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public FirstDayOfWeek getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(FirstDayOfWeek firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public DateFormatPattern getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormatPattern dateFormat) {
        this.dateFormat = dateFormat;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserId getUser() {
        return user;
    }

    public void setUser(UserId user) {
        this.user = user;
    }

    public static UserPreference defaultFor(UserId userId) {
        OffsetDateTime now = OffsetDateTime.now();

        return new UserPreference(
                new PreferenceId(userId.value()),
                UserPreferenceDefaults.LOCALE,
                UserPreferenceDefaults.CURRENCY,
                UserPreferenceDefaults.TIMEZONE,
                UserPreferenceDefaults.THEME,
                UserPreferenceDefaults.FIRST_DAY_OF_WEEK,
                UserPreferenceDefaults.DATE_FORMAT,
                now,
                now,
                userId
        );
    }
}