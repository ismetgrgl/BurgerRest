package org.burgerapp.mapper;


import org.burgerapp.dto.request.UserSaveRequestDto;
import org.burgerapp.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE= Mappers.getMapper(UserMapper.class);

    UserProfile toUserProfile(UserSaveRequestDto dto);
}