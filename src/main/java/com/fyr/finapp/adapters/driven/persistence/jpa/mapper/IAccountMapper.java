package com.fyr.finapp.adapters.driven.persistence.jpa.mapper;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.domain.shared.vo.Color;
import com.fyr.finapp.domain.shared.vo.Icon;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.model.user.vo.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface IAccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "type", source = "type", qualifiedByName = "accountTypeToString")
    @Mapping(target = "initialBalance", source = "initialBalance.amount")
    @Mapping(target = "currentBalance", source = "currentBalance.amount")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "icon", source = "icon.name")
    @Mapping(target = "color", source = "color.value")
    @Mapping(target = "isDefault", source = "defaultAccount")
    @Mapping(target = "isArchived", source = "archived")
    @Mapping(target = "excludeFromTotal", source = "excludeFromTotal")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    AccountEntity toEntity(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "type", source = "type", qualifiedByName = "accountTypeToString")
    @Mapping(target = "initialBalance", source = "initialBalance.amount")
    @Mapping(target = "currentBalance", source = "currentBalance.amount")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "icon", source = "icon.name")
    @Mapping(target = "color", source = "color.value")
    @Mapping(target = "isDefault", source = "defaultAccount")
    @Mapping(target = "isArchived", source = "archived")
    @Mapping(target = "excludeFromTotal", source = "excludeFromTotal")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    void updateEntityFromDomain(Account domain, @MappingTarget AccountEntity entity);

    default Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        return Account.reconstruct(
                AccountId.of(entity.getId().toString()),
                UserId.of(entity.getUser().getId().toString()),
                AccountName.of(entity.getName()),
                AccountType.fromString(entity.getType()),
                Money.of(entity.getInitialBalance(), entity.getCurrency()),
                Money.of(entity.getCurrentBalance(), entity.getCurrency()),
                Icon.of(entity.getIcon()),
                Color.of(entity.getColor()),
                entity.getIsDefault(),
                entity.getIsArchived(),
                entity.getExcludeFromTotal(),
                entity.getCreatedAt().toInstant(),
                entity.getUpdatedAt().toInstant()
        );
    }

    @Named("accountTypeToString")
    default String accountTypeToString(AccountType type) {
        return type.name();
    }

    @Named("stringToAccountType")
    default AccountType stringToAccountType(String type) {
        return AccountType.fromString(type);
    }

    @Named("uuidToAccountId")
    default AccountId uuidToAccountId(UUID id) {
        return AccountId.of(id.toString());
    }

    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID id) {
        return UserId.of(id.toString());
    }

    @Named("stringToAccountName")
    default AccountName stringToAccountName(String name) {
        return AccountName.of(name);
    }

    @Named("entityToMoney")
    default Money entityToMoney(AccountEntity entity) {
        return Money.of(entity.getInitialBalance(), entity.getCurrency());
    }

    @Named("stringToIcon")
    default Icon stringToIcon(String icon) {
        return Icon.of(icon);
    }

    @Named("stringToColor")
    default Color stringToColor(String color) {
        return Color.of(color);
    }

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant value) {
        return value == null ? null : OffsetDateTime.ofInstant(value, ZoneOffset.UTC);
    }

    @Named("offsetDateTimeToInstant")
    default Instant offsetDateTimeToInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }
}
