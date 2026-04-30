package com.fyr.finapp.adapters.driven.persistence.jpa.mapper;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.RecurringTransactionEntity;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.recurring.RecurringTransaction;
import com.fyr.finapp.domain.model.recurring.RecurringTransactionId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.RecurringFrequency;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface IRecurringTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "toAccount", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "amount.currency.code")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "frequency", source = "frequency")
    @Mapping(target = "nextDueDate", source = "nextDueDate")
    @Mapping(target = "lastGeneratedAt", source = "lastGeneratedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "deletedAt", source = "deletedAt", qualifiedByName = "instantToOffsetDateTime")
    void updateEntityFromDomain(RecurringTransaction domain, @MappingTarget RecurringTransactionEntity entity);

    default RecurringTransaction toDomain(RecurringTransactionEntity entity) {
        if (entity == null) return null;

        CategoryId categoryId = entity.getCategory() != null
                ? CategoryId.of(entity.getCategory().getId().toString())
                : null;
        AccountId toAccountId = entity.getToAccount() != null
                ? AccountId.of(entity.getToAccount().getId().toString())
                : null;

        return RecurringTransaction.reconstruct(
                RecurringTransactionId.of(entity.getId().toString()),
                UserId.of(entity.getUser().getId().toString()),
                AccountId.of(entity.getAccount().getId().toString()),
                toAccountId,
                categoryId,
                TransactionType.fromString(entity.getType()),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getDescription(),
                entity.getNote(),
                RecurringFrequency.fromString(entity.getFrequency()),
                entity.getNextDueDate(),
                offsetDateTimeToInstant(entity.getLastGeneratedAt()),
                entity.isActive(),
                offsetDateTimeToInstant(entity.getCreatedAt()),
                offsetDateTimeToInstant(entity.getUpdatedAt()),
                offsetDateTimeToInstant(entity.getDeletedAt())
        );
    }

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant value) {
        return value == null ? null : OffsetDateTime.ofInstant(value, ZoneOffset.UTC);
    }

    default Instant offsetDateTimeToInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }
}
