package com.fyr.finapp.adapters.driven.persistence.jpa.mapper;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserPreferenceEntity;
import com.fyr.finapp.domain.model.user.UserPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {VoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUserPreferenceEntityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserPreferenceEntity toEntity(UserPreference user);

    UserPreference toUser(UserPreferenceEntity userPreferenceEntity);
}
