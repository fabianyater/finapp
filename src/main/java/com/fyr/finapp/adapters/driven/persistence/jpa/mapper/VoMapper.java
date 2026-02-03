package com.fyr.finapp.adapters.driven.persistence.jpa.mapper;


import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.domain.model.common.vo.Currency;
import com.fyr.finapp.domain.model.user.vo.*;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VoMapper {
    // ---------- IDs ----------
    default UUID map(UserId id) {
        return id == null ? null : id.value();
    }

    default UserId map(UUID id) {
        return id == null ? null : new UserId(id);
    }

    default UUID map(PreferenceId id) {
        return id == null ? null : id.value();
    }

    default PreferenceId mapPreferenceId(UUID id) {
        return id == null ? null : new PreferenceId(id);
    }

    // ---------- Strings / VOs ----------
    default String map(Email email) {
        return email == null ? null : email.value();
    }

    default Email mapEmail(String email) {
        return email == null ? null : new Email(email);
    }

    default String map(Username username) {
        return username == null ? null : username.value();
    }

    default Username mapUsername(String username) {
        return username == null ? null : new Username(username);
    }

    default String map(PersonName name) {
        return name == null ? null : name.value();
    }

    default PersonName mapPersonName(String name) {
        return name == null ? null : new PersonName(name);
    }

    default String map(PasswordHash hash) {
        return hash == null ? null : hash.value();
    }

    default PasswordHash mapPasswordHash(String hash) {
        return hash == null ? null : new PasswordHash(hash);
    }

    // ---------- Preferences ----------
    default String map(LocaleTag locale) {
        return locale == null ? null : locale.value();
    }

    default LocaleTag mapLocaleTag(String locale) {
        return locale == null ? null : new LocaleTag(locale);
    }

    default String map(Currency currency) {
        return currency == null ? null : currency.code();
    }

    default Currency mapCurrencyCode(String currency) {
        return currency == null ? null : new Currency(currency);
    }

    default String map(TimezoneId timezone) {
        return timezone == null ? null : timezone.value();
    }

    default TimezoneId mapTimezoneId(String timezone) {
        return timezone == null ? null : new TimezoneId(timezone);
    }

    default short map(FirstDayOfWeek firstDay) {
        return firstDay == null ? 0 : firstDay.value();
    }

    default FirstDayOfWeek mapFirstDayOfWeek(short value) {
        if (value == 0) return null;
        return new FirstDayOfWeek(value);
    }

    default String map(DateFormatPattern fmt) {
        return fmt == null ? null : fmt.value();
    }

    default DateFormatPattern mapDateFormatPattern(String fmt) {
        return fmt == null ? null : new DateFormatPattern(fmt);
    }

    default UserId toUserId(UserEntity entity) {
        if (entity == null) return null;
        UUID id = entity.getId();
        return id == null ? null : new UserId(id);
    }

    default UserEntity toUserEntity(UserId userId) {
        if (userId == null || userId.value() == null) return null;
        UserEntity e = new UserEntity();
        e.setId(userId.value());
        return e;
    }
}
