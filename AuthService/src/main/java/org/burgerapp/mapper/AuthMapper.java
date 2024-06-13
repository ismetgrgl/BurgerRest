package org.burgerapp.mapper;

import org.burgerapp.dto.request.RegisterRequestDto;
import org.burgerapp.dto.request.UserSaveRequestDto;
import org.burgerapp.dto.response.RegisterResponseDto;
import org.burgerapp.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    Auth registerRequestDtoToAuth(RegisterRequestDto dto);

    RegisterResponseDto authToDto(Auth auth);

    //    LoginResponseDto toLoginResponseDto(Auth auth);
    @Mapping(source = "id", target = "authId")
    UserSaveRequestDto toUserSaveRequestDto(Auth auth);


}
