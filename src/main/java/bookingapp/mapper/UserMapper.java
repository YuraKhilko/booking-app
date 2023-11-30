package bookingapp.mapper;

import bookingapp.config.MapperConfig;
import bookingapp.dto.user.UserRegistrationRequestDto;
import bookingapp.dto.user.UserResponseDto;
import bookingapp.dto.user.UserUpdateRequestDto;
import bookingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequestDto requestDto);

    User toModel(UserUpdateRequestDto requestDto);

    UserResponseDto toResponseDto(User user);
}
