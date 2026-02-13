package com.fyr.finapp.adapters.driven.persistence.jpa.mapper;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.TransactionEntity;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.transaction.TransactionId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ITransactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "occurredOn", source = "occurredOn", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    TransactionEntity toEntity(Transaction transaction);

    default Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Transaction.reconstruct(
                TransactionId.of(entity.getId().toString()),
                TransactionType.fromString(entity.getType()),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getDescription(),
                entity.getNote(),
                offsetDateTimeToInstant(entity.getOccurredOn()),
                offsetDateTimeToInstant(entity.getCreatedAt()),
                offsetDateTimeToInstant(entity.getUpdatedAt()),
                UserId.of(entity.getUser().getId().toString()),
                CategoryId.of(entity.getCategories().getId().toString()),
                AccountId.of(entity.getAccounts().getId().toString())

        );
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
