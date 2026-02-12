package com.fyr.finapp.adapters.driven.persistence.jpa.mapper;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.CategoryEntity;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Color;
import com.fyr.finapp.domain.shared.vo.Icon;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ICategoryEntityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "color", source = "color.value")
    @Mapping(target = "icon", source = "icon.name")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    CategoryEntity toEntity(Category category);

    Iterable<CategoryEntity> toEntityList(Iterable<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "color", source = "color.value")
    @Mapping(target = "icon", source = "icon.name")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    void updateEntityFromDomain(Category domain, @MappingTarget CategoryEntity entity);

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant value) {
        return value == null ? null : OffsetDateTime.ofInstant(value, ZoneOffset.UTC);
    }

    @Named("offsetDateTimeToInstant")
    default Instant offsetDateTimeToInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }

    default Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return Category.reconstruct(
                CategoryId.of(entity.getId().toString()),
                UserId.of(entity.getUser().getId().toString()),
                CategoryName.of(entity.getName()),
                TransactionType.valueOf(entity.getType()),
                Color.of(entity.getColor()),
                Icon.of(entity.getIcon()),
                offsetDateTimeToInstant(entity.getCreatedAt()),
                offsetDateTimeToInstant(entity.getUpdatedAt())
        );
    }
}
