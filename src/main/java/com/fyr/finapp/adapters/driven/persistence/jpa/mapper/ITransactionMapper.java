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
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ITransactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "toAccount", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "occurredOn", source = "occurredOn", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "deletedAt", source = "deletedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToSet")
    TransactionEntity toEntity(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "toAccount", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "occurredOn", source = "occurredOn", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "deletedAt", source = "deletedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToSet")
    void updateEntityFromDomain(Transaction domain, @MappingTarget TransactionEntity entity);

    default Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        var cat = entity.getCategories();
        CategoryId categoryId = cat != null ? CategoryId.of(cat.getId().toString()) : null;
        String categoryName  = cat != null ? cat.getName()  : null;
        String categoryColor = cat != null ? cat.getColor() : null;
        String categoryIcon  = cat != null ? cat.getIcon()  : null;
        AccountId toAccountId = entity.getToAccount() != null
                ? AccountId.of(entity.getToAccount().getId().toString())
                : null;
        var user = entity.getUser();
        String creatorName = user.getName() + " " + user.getSurname();
        return Transaction.reconstruct(
                TransactionId.of(entity.getId().toString()),
                TransactionType.fromString(entity.getType()),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getDescription(),
                entity.getNote(),
                offsetDateTimeToInstant(entity.getOccurredOn()),
                offsetDateTimeToInstant(entity.getCreatedAt()),
                offsetDateTimeToInstant(entity.getUpdatedAt()),
                offsetDateTimeToInstant(entity.getDeletedAt()),
                UserId.of(user.getId().toString()),
                creatorName,
                categoryId,
                categoryName,
                categoryColor,
                categoryIcon,
                AccountId.of(entity.getAccounts().getId().toString()),
                toAccountId,
                entity.getTags() != null ? new ArrayList<>(entity.getTags()) : new ArrayList<>()
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

    @Named("listToSet")
    default Set<String> listToSet(List<String> list) {
        return list == null ? new HashSet<>() : new HashSet<>(list);
    }

}
