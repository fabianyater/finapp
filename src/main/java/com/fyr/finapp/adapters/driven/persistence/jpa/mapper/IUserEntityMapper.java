package com.fyr.finapp.adapters.driven.persistence.jpa.mapper;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.domain.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {VoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IUserEntityMapper {
    @Mapping(target = "accountEntities", ignore = true)
    UserEntity toEntity(User user);

    User toUser(UserEntity userEntity);
}
