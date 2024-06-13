package org.burgerapp.mapper;

import javax.annotation.processing.Generated;
import org.burgerapp.dto.request.RegisterRequestDto;
import org.burgerapp.dto.request.UserSaveRequestDto;
import org.burgerapp.dto.response.RegisterResponseDto;
import org.burgerapp.entity.Auth;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-13T10:53:16+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public Auth registerRequestDtoToAuth(RegisterRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Auth.AuthBuilder<?, ?> auth = Auth.builder();

        auth.username( dto.getUsername() );
        auth.email( dto.getEmail() );
        auth.password( dto.getPassword() );

        return auth.build();
    }

    @Override
    public RegisterResponseDto authToDto(Auth auth) {
        if ( auth == null ) {
            return null;
        }

        RegisterResponseDto.RegisterResponseDtoBuilder registerResponseDto = RegisterResponseDto.builder();

        registerResponseDto.id( auth.getId() );
        registerResponseDto.code( auth.getCode() );
        registerResponseDto.username( auth.getUsername() );

        return registerResponseDto.build();
    }

    @Override
    public UserSaveRequestDto toUserSaveRequestDto(Auth auth) {
        if ( auth == null ) {
            return null;
        }

        UserSaveRequestDto.UserSaveRequestDtoBuilder userSaveRequestDto = UserSaveRequestDto.builder();

        userSaveRequestDto.authId( auth.getId() );
        userSaveRequestDto.username( auth.getUsername() );
        userSaveRequestDto.email( auth.getEmail() );

        return userSaveRequestDto.build();
    }
}
